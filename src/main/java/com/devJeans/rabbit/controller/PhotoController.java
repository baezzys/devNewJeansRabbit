package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.dto.PhotoDto;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    private final PhotoService photoService;

    private final AccountService accountService;

    public PhotoController(PhotoService photoService, AccountService accountService) {
        this.photoService = photoService;
        this.accountService = accountService;
    }


    @PostMapping("/upload")
    public ResponseEntity<PhotoDto> uploadPhoto(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            Account user = accountService.getAccount(Long.valueOf(principal.getName()));

            Photo savedPhoto = photoService.uploadPhoto(file, user);
            return new ResponseEntity<>(PhotoDto.of(savedPhoto), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<PhotoDto> likePhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.likePhoto();
        photoService.savePhoto(photo);
        return new ResponseEntity<>(PhotoDto.of(photo), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.addVisitCount();

        return ResponseEntity.ok(PhotoDto.of(photo));
    }
}
