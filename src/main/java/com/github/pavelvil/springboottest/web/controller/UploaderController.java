package com.github.pavelvil.springboottest.web.controller;

import com.github.pavelvil.springboottest.model.File;
import com.github.pavelvil.springboottest.model.User;
import com.github.pavelvil.springboottest.model.dto.FileData;
import com.github.pavelvil.springboottest.model.dto.SharedData;
import com.github.pavelvil.springboottest.model.dto.UserFilesData;
import com.github.pavelvil.springboottest.service.FileService;
import com.github.pavelvil.springboottest.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@Log4j2
public class UploaderController {

    private final UserService userService;

    private final FileService fileService;

    @Value("#{environment['com.github.pavelvil.uploader.directory']}")
    private String fileDirectory;

    public UploaderController(final UserService userService, final FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping(value = "/file")
    public ResponseEntity<UserFilesData> getAllFiles(@AuthenticationPrincipal User user) {
        UserFilesData data = new UserFilesData();
        data.setOwned(user.getOwnFiles().stream().map(f -> new FileData(f.getId(), f.getData(), f.getName())).collect(Collectors.toSet()));
        data.setShared(user.getSharedFiles().stream().map(f -> new FileData(f.getId(), f.getData(), f.getName())).collect(Collectors.toSet()));
        return ResponseEntity.ok(data);
    }

    @GetMapping(value = "/file/{id}")
    public ResponseEntity<?> getFile(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        Optional<File> optionalFile = fileService.findById(id);

        if (!optionalFile.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<File> optionalUserFile = userService.findUserFile(user, optionalFile.get());

        if (!optionalUserFile.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        File userFile = optionalUserFile.get();
        FileData fileData = new FileData(userFile.getId(), userFile.getData(), userFile.getName());

        log.info(String.format("Get file. File Data is: %s", fileData));

        return ResponseEntity.ok(fileData);
    }

    @PostMapping(value = "/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) throws URISyntaxException {
        File newFile = saveFile(file, user);
        log.info(String.format("File '%s' has been stored", newFile.getName()));
        return ResponseEntity.created(new URI("/api/file/" + newFile.getId())).body(newFile.getId());
    }

    @PostMapping(value = "/share")
    public ResponseEntity<?> sharedFile(@RequestBody SharedData sharedData, @AuthenticationPrincipal User owner) {
        Optional<User> optionalUserReceiver = userService.findByUsername(sharedData.getEmail());
        Optional<File> optionalFile = fileService.findById(sharedData.getFileId());

        if (!optionalUserReceiver.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (!optionalFile.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User userReceiver = optionalUserReceiver.get();
        File file = optionalFile.get();

        if (!userService.shareFile(owner, userReceiver, file)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(String.format("Cannot share file between users." +
                "\n Owner: %s\n Receiver: %s", owner.getId(), userReceiver.getId()));
        }

        log.info(String.format("File '%s' has been shared", file.getName()));

        return ResponseEntity.ok(String.format("File '%s' with id '%s' was shared for '%s'", file.getName(),
            file.getId(), userReceiver.getUsername()));
    }

    private File saveFile(MultipartFile file, User user) {
        File newFile = new File();
        try {
            newFile.setData(file.getBytes());
            newFile.setName(file.getOriginalFilename());
            newFile.setOwned(user);
        } catch (IOException e) {
            log.warn("Exception trying to save file");
            throw new RuntimeException(e);
        }

        fileService.save(newFile);
        user.addOwnFile(newFile);
        userService.save(user);

        log.info(String.format("File created: %s", newFile));

        fileService.saveFileToDirectory(fileDirectory, newFile);

        return newFile;
    }
}
