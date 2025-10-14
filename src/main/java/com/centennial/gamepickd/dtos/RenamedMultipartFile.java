package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public record RenamedMultipartFile(
        @NotNull
        MultipartFile file,
        @NotBlank(message = "Filename cannot be blank")
        String newFilename
) implements MultipartFile {

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return newFilename;
    }

    @Override
    public String getContentType() {
        return file.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return file.isEmpty();
    }

    @Override
    public long getSize() {
        return file.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return file.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        file.transferTo(dest);
    }
}
