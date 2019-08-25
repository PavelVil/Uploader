package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.model.File;
import com.github.pavelvil.springboottest.repository.FileRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public FileServiceImpl(final FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File save(final File file) {
        return fileRepository.save(file);
    }

    @Override
    public Optional<File> findById(final String id) {
        return fileRepository.findById(id);
    }

    @Override
    public Optional<File> findByName(final String name) {
        return fileRepository.findByName(name);
    }

    @Override
    public void remove(final String id) {
        fileRepository.deleteById(id);
    }

    @Override
    public List<File> getAll() {
        return fileRepository.findAll();
    }

    @Override
    public void saveFileToDirectory(final String directoryPath, final File file) {
        Path directory = Paths.get(directoryPath);
        Path filePath = Paths.get(String.format("%s/%s", directory.toString(), file.getName()));
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Files.write(filePath, file.getData());
        } catch (IOException e) {
            log.error(String.format("Cannot save file to directory '%s'", directory));
            throw new RuntimeException(e);
        }
    }
}
