package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.dto.PhotoDto;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static com.devJeans.rabbit.bind.ApiResult.failed;
import static com.devJeans.rabbit.bind.ApiResult.succeed;

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
    public ApiResult uploadPhoto(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            Account user = accountService.getAccount(Long.valueOf(principal.getName()));

            Photo savedPhoto = photoService.uploadPhoto(file, user);
            return succeed(PhotoDto.of(savedPhoto));
        } catch (Exception e) {
            return failed(e.getMessage());
        }
    }

    @PostMapping("/like/{id}")
    public ApiResult<PhotoDto> likePhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.likePhoto();
        photoService.savePhoto(photo);
        return succeed(PhotoDto.of(photo));
    }

    @PostMapping("/like/cancle/{id}")
    public ApiResult<PhotoDto> cancleLikePhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.cancleLikePhoto();
        photoService.savePhoto(photo);
        return succeed(PhotoDto.of(photo));
    }

    @GetMapping("/{id}")
    public ApiResult getPhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.addVisitCount();

        return succeed(PhotoDto.of(photo));
    }

    @DeleteMapping("/{id}}")
    public ApiResult deletePhoto(Principal principal, @PathVariable("id") Long photoId) {
        photoService.deletePhoto(Long.valueOf(principal.getName()), photoId);
        return succeed("사진이 정상적으로 삭제 되었습니다. 사진 id : " + photoId);
    }

    @GetMapping("/all/ranked")
    public ApiResult getRankedPhoto(@RequestParam(defaultValue = "0") int page) {
        Page<Photo> photoPage = photoService.findAllPhoto(page);
        return succeed(photoPage);
    }
}
