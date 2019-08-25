package com.github.pavelvil.springboottest.repository;

import com.github.pavelvil.springboottest.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {

    Optional<File> findByName(String name);

}
