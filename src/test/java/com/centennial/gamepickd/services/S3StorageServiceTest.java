package com.centennial.gamepickd.services;

import com.centennial.gamepickd.config.StorageProperties;
import com.centennial.gamepickd.services.impl.S3StorageService;
import com.centennial.gamepickd.util.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class S3StorageServiceTest {

    private S3Client s3Client;
    private StorageProperties properties;
    private ResourceLoader resourceLoader;
    private S3StorageService s3StorageService;


    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        resourceLoader = mock(ResourceLoader.class);
        properties = new StorageProperties();
        properties.setLocation(bucketName);
        s3StorageService = new S3StorageService(s3Client, properties, resourceLoader);
    }


    @Test
    void store_WithValidFile_ShouldUploadToS3() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("test.txt", "test.txt", "text/plain", "Hello".getBytes());

        // Act & Assert
        assertDoesNotThrow(() -> s3StorageService.store(file));

        verify(s3Client).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        );
    }

    @Test
    void store_WithEmptyFile_ShouldThrowStorageException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("empty.txt", new byte[0]);

        // Act & Assert
        assertThrows(Exceptions.StorageException.class, () -> s3StorageService.store(emptyFile));
    }

    @Test
    void store_WhenIOExceptionOccurs_ShouldThrowStorageException() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getSize()).thenReturn(5L);
        when(file.getInputStream()).thenThrow(new IOException("Failed to read input stream"));

        // Act & Assert
        Exceptions.StorageException exception = assertThrows(Exceptions.StorageException.class, () -> s3StorageService.store(file));
        assertTrue(exception.getMessage().contains("Failed to store file to S3"));
        assertInstanceOf(IOException.class, exception.getCause());
    }


    @Test
    void loadAll_ShouldReturnListOfPaths() {
        // Arrange
        ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(S3Object.builder().key("file1.txt").build(),
                        S3Object.builder().key("file2.txt").build())
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

        // Act
        Stream<Path> files = s3StorageService.loadAll();

        // Assert
        List<Path> fileList = files.toList();
        assertEquals(2, fileList.size());
        assertEquals("file1.txt", fileList.getFirst().toString());
    }

    @Test
    void loadAll_WhenS3ExceptionOccurs_ShouldThrowStorageException() {
        // Arrange
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenThrow(S3Exception.builder().message("S3 error").build());

        // Act & Assert
        Exceptions.StorageException exception = assertThrows(Exceptions.StorageException.class, () -> s3StorageService.loadAll());
        assertTrue(exception.getMessage().contains("Failed to list files in S3"));
        assertInstanceOf(S3Exception.class, exception.getCause());
    }


    @Test
    void load_ShouldReturnPath() {
        // Act
        java.nio.file.Path path = s3StorageService.load("file.txt");

        // Assert
        assertEquals("file.txt", path.toString());
    }

    @Test
    void loadAsResource_WithExistingFile_ShouldReturnResource() throws Exceptions.StorageFileNotFoundException {
        // Arrange
        String filename = "file.txt";
        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("s3://" + bucketName + "/" + filename)).thenReturn(resource);

        // Act
        Resource result = s3StorageService.loadAsResource(filename);

        // Assert
        assertEquals(resource, result);
    }

    @Test
    void loadAsResource_WhenS3ExceptionOccurs_ShouldThrowStorageFileNotFoundException() {
        // Arrange
        String filename = "file.txt";
        when(resourceLoader.getResource("s3://" + bucketName + "/" + filename))
                .thenThrow(S3Exception.builder().message("not found").build());

        // Act & Assert
        assertThrows(Exceptions.StorageFileNotFoundException.class, () -> s3StorageService.loadAsResource(filename));
    }

    @Test
    void deleteAll_ShouldDeleteAllObjects() {
        // Arrange
        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(S3Object.builder().key("file1.txt").build(),
                        S3Object.builder().key("file2.txt").build())
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);
        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        // Act & Assert
        assertDoesNotThrow(() -> s3StorageService.deleteAll());

        verify(s3Client).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    void deleteAll_WhenS3ExceptionOccurs_ShouldThrowStorageException() {
        // Arrange
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenThrow(S3Exception.builder().message("S3 failed").build());

        // Act & Assert
        assertThrows(Exceptions.StorageException.class, () -> s3StorageService.deleteAll());
    }

    @Test
    void deleteAll_WhenNoObjectsExist_ShouldNotCallDeleteObjects() {
        // Arrange
        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(List.of()) // Empty list
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        // Act & Assert
        assertDoesNotThrow(() -> s3StorageService.deleteAll());

        // Ensure deleteObjects is never called
        verify(s3Client, never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

}
