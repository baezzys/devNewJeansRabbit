package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.dto.PhotoDto;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.io.IOException;
import java.security.Principal;

import static com.devJeans.rabbit.bind.ApiResult.succeed;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "https://devjeans.dev-hee.com", "https://www.devnewjeans.com"},  allowCredentials = "true")
@RequestMapping("/photo")
public class PhotoController {

    private final PhotoService photoService;

    private final AccountService accountService;

    public PhotoController(PhotoService photoService, AccountService accountService) {
        this.photoService = photoService;
        this.accountService = accountService;
    }


    @PostMapping("/upload")
    public ApiResult<PhotoDto> uploadPhoto(@RequestParam("image") MultipartFile image, @RequestParam("thumbnail") MultipartFile thumbnail, @RequestParam("photo_title") String photoTitle,Principal principal) throws IOException {

        Account user = accountService.getAccount(Long.valueOf(principal.getName()));

        Photo savedPhoto = photoService.uploadPhoto(image, thumbnail, photoTitle, user);
        return succeed(PhotoDto.of(savedPhoto));

    }
    @PostMapping("/like/{id}")
    @Transactional
    public ApiResult<PhotoDto> likePhoto(@PathVariable("id") Long photoId, Principal principal) {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));

        Photo photo = photoService.findPhotoById(photoId);
        user.addLikedPhoto(photo);
        synchronized(photo) {
            photo.likePhoto();
            photoService.savePhoto(photo);
        }
        return succeed(PhotoDto.of(photo));
    }

    @PostMapping("/like/cancle/{id}")
    @Transactional
    public ApiResult<PhotoDto> cancelLikePhoto(@PathVariable("id") Long photoId, Principal principal) {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));

        Photo photo = photoService.findPhotoById(photoId);
        user.removeLikedPhoto(photo);
        synchronized(photo) {
            photo.cancelLikePhoto();
            photoService.savePhoto(photo);
        }

        photoService.savePhoto(photo);
        return succeed(PhotoDto.of(photo));
    }

    @GetMapping("/{id}")
    public ApiResult<PhotoDto> getPhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.addVisitCount();

        return succeed(PhotoDto.of(photo));
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> deletePhoto(Principal principal, @PathVariable("id") Long photoId) {
        photoService.deletePhoto(Long.valueOf(principal.getName()), photoId);
        return succeed("사진이 정상적으로 삭제 되었습니다. 사진 id : " + photoId);
    }

    @GetMapping("/all/ranked")
    public ApiResult<Page<PhotoDto>> getRankedPhotos(@RequestParam(defaultValue = "0") int page) {
        Page<Photo> photoPage = photoService.findAllPhotoOrderByLikeCount(page);
        Page<PhotoDto> photoDtoPage = photoPage.map(PhotoDto::of);
        return succeed(photoDtoPage);
    }

    @GetMapping("/all/latest")
    public ApiResult<Page<PhotoDto>> getLatestPhotos(@RequestParam(defaultValue = "0") int page) {
        Page<Photo> photoPage = photoService.findAllPhotoOrderByLatest(page);
        Page<PhotoDto> photoDtoPage = photoPage.map(PhotoDto::of);
        return succeed(photoDtoPage);
    }

    @GetMapping("/user/like")
    public ApiResult<Boolean> isLikePhoto(Principal principal, Long photoId) {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));

        if (user.getLikedPhotoIds().contains(photoId)) {
            return succeed(Boolean.TRUE);
        }
        return succeed(Boolean.FALSE);
    }

}
