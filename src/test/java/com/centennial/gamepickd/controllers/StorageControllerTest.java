package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.util.Exceptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageService storageService;

    @Test
    void getImage_ShouldReturnImage_WhenFileExists() throws Exception {
        // Arrange
        String filename = "test-image.jpg";
        byte[] imageContent = "fake image content".getBytes();

        // Create a custom Resource implementation that knows its filename
        Resource resource = new ByteArrayResource(imageContent) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        when(storageService.loadAsResource(filename))
                .thenReturn(resource);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/images/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""))
                .andExpect(content().bytes(imageContent));
    }

    @Test
    void getImage_ShouldReturnNotFound_WhenFileDoesNotExist() throws Exception {
        // Arrange
        String filename = "non-existent-image.jpg";

        // Simulate the service throwing the exception
        when(storageService.loadAsResource(filename))
                .thenThrow(new Exceptions.StorageFileNotFoundException("File not found"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/images/{filename}", filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImage_ShouldReturnBadRequest_WhenStorageExceptionIsThrown() throws Exception {
        // Arrange
        String filename = "non-existent-image.jpg";

        // This works only if StorageException is a RuntimeException
        when(storageService.loadAsResource(filename))
                .thenThrow(new Exceptions.StorageException("Storage Error"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/images/{filename}", filename))
                .andExpect(status().isBadRequest());
    }

}
