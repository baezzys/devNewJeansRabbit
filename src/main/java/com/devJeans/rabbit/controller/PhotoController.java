package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.domain.Report;
import com.devJeans.rabbit.dto.PhotoDto;
import com.devJeans.rabbit.repository.PhotoRepository;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;


import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.Principal;

import static com.devJeans.rabbit.bind.ApiResult.succeed;
import static com.google.common.base.Preconditions.checkArgument;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "https://devjeans.dev-hee.com", "https://www.devnewjeans.com", "https://stg-devjeans.dev-hee.com"}, allowCredentials = "true")
@RequestMapping("/photo")
public class PhotoController {

    private final PhotoService photoService;

    private final AccountService accountService;
    private final PhotoRepository photoRepository;

    public PhotoController(PhotoService photoService, AccountService accountService,
                           PhotoRepository photoRepository) {
        this.photoService = photoService;
        this.accountService = accountService;
        this.photoRepository = photoRepository;
    }


    @PostMapping("/upload")
    @Transactional
    public ApiResult<PhotoDto> uploadPhoto(@RequestParam("image") MultipartFile image, @RequestParam("thumbnail") MultipartFile thumbnail, @RequestParam("photo_title") String photoTitle, Principal principal) throws IOException {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));

        checkArgument(user.getIsBlockedUser().equals(Boolean.FALSE), "차단된 유저는 해당 기능을 사용할 수 없습니다.");

        Photo savedPhoto = photoService.uploadPhoto(image, thumbnail, photoTitle, user);
        return succeed(PhotoDto.of(savedPhoto));

    }

    @PostMapping("/like/{id}")
    @Retryable(value = {StaleStateException.class}, backoff = @Backoff(delay = 1000), maxAttempts = 10)
    @Transactional
    public ApiResult<PhotoDto> likePhoto(@PathVariable("id") Long photoId, Principal principal) {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));
        Photo photo = photoService.findPhotoById(photoId);

        photoService.likePhoto(photo, user);

        return succeed(PhotoDto.of(photo));
    }

    @PostMapping("/like/cancel/{id}")
    @Retryable(value = {StaleStateException.class}, backoff = @Backoff(delay = 1000), maxAttempts = 10)
    @Transactional
    public ApiResult<PhotoDto> cancelLikePhoto(@PathVariable("id") Long photoId, Principal principal) {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));
        Photo photo = photoService.findPhotoById(photoId);

        photoService.cancelLikePhoto(photo, user);

        return succeed(PhotoDto.of(photo));
    }

    @GetMapping("/{id}")
    @Transactional
    public ApiResult<PhotoDto> getPhoto(@PathVariable("id") Long photoId) {
        Photo photo = photoService.findPhotoById(photoId);
        photo.addVisitCount();
        photoRepository.save(photo);

        return succeed(PhotoDto.of(photo));
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> deletePhoto(Principal principal, @PathVariable("id") Long photoId) {
        photoService.deletePhoto(Long.valueOf(principal.getName()), photoId);
        return succeed("사진이 정상적으로 삭제 되었습니다. 사진 id : " + photoId);
    }

    @GetMapping("/all/ranked")
    @Transactional
    public ApiResult<Page<PhotoDto>> getRankedPhotos(@RequestParam(defaultValue = "0") int page) {
        Page<Photo> photoPage = photoService.findAllPhotoOrderByLikeCount(page);
        Page<PhotoDto> photoDtoPage = photoPage.map(PhotoDto::of);
        return succeed(photoDtoPage);
    }

    @GetMapping("/all/latest")
    @Transactional
    public ApiResult<Page<PhotoDto>> getLatestPhotos(@RequestParam(defaultValue = "0") int page) {
        Page<Photo> photoPage = photoService.findAllPhotoOrderByLatest(page);
        Page<PhotoDto> photoDtoPage = photoPage.map(PhotoDto::of);
        return succeed(photoDtoPage);
    }

    @GetMapping("/user/like")
    @Transactional
    public ApiResult<Boolean> isLikePhoto(Principal principal, Long photoId) {
        Account user = accountService.getAccount(Long.valueOf(principal.getName()));
        Photo photo = photoRepository.findById(photoId).orElseThrow(EntityNotFoundException::new);

        if (user.getLikedPhotos().contains(photo)) {
            return succeed(Boolean.TRUE);
        }
        return succeed(Boolean.FALSE);
    }

    @PostMapping("/report/{id}")
    @Transactional
    public ApiResult<String> reportPhoto(Principal principal, @PathVariable("id") Long photoId, Report.ReportType reportType) {
        checkArgument(reportType != null, "report type이 명시되어야 합니다.");

        Account user = accountService.getAccount(Long.valueOf(principal.getName()));
        Photo photo = photoRepository.findById(photoId).orElseThrow(EntityNotFoundException::new);

        photoService.reportPhoto(user, photo, reportType);
        return succeed("해당 사진이 성공적으로 신고되었습니다. id : " + photoId);
    }
}
