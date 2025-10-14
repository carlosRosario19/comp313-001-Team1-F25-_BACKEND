package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.config.StorageProperties;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.util.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final ResourceLoader resourceLoader;

    @Autowired
    public S3StorageService(S3Client s3Client, StorageProperties properties, ResourceLoader resourceLoader) {
        this.s3Client = s3Client;
        this.bucketName = properties.getLocation(); // Use location as bucket name
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void store(MultipartFile file) throws Exceptions.StorageException {
        try {
            if (file.isEmpty()) {
                throw new Exceptions.StorageException("Failed to store empty file.");
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new Exceptions.StorageException("Failed to store file to S3", e);
        }
    }

    @Override
    public Stream<Path> loadAll() throws Exceptions.StorageException {
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            return listObjectsResponse.contents().stream()
                    .map(S3Object::key)
                    .map(Paths::get);
        } catch (S3Exception e) {
            throw new Exceptions.StorageException("Failed to list files in S3", e);
        }
    }

    @Override
    public Path load(String filename) {
        return Paths.get(filename);
    }

    @Override
    public Resource loadAsResource(String filename) throws Exceptions.StorageFileNotFoundException {
        try {
            return resourceLoader.getResource("s3://" + bucketName + "/" + filename);
        } catch (S3Exception e) {
            throw new Exceptions.StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            // List all objects
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            // Delete all objects
            if (!listObjectsResponse.contents().isEmpty()) {
                List<ObjectIdentifier> objectsToDelete = listObjectsResponse.contents().stream()
                        .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
                        .toList();

                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(d -> d.objects(objectsToDelete))
                        .build();

                s3Client.deleteObjects(deleteRequest);
            }
        } catch (S3Exception e) {
            throw new Exceptions.StorageException("Failed to delete files from S3", e);
        }
    }
}
