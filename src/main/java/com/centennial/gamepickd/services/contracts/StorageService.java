package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.util.Exceptions;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void store(MultipartFile file) throws Exceptions.StorageException;
    Stream<Path> loadAll() throws Exceptions.StorageException;
    Path load(String filename);
    Resource loadAsResource(String filename) throws Exceptions.StorageFileNotFoundException;
    void deleteAll();
}
