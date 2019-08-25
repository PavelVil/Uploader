package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.model.File;

import java.util.List;
import java.util.Optional;

public interface FileService {

    File save(File file);

    Optional<File> findById(String id);

    Optional<File> findByName(String name);

    void remove(String id);

    List<File> getAll();

    void saveFileToDirectory(String directory, File file);

}
