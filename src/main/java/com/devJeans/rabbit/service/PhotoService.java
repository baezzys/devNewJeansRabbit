package com.devJeans.rabbit.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.repository.AccountRepository;
import com.devJeans.rabbit.repository.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PhotoService {
    private final AccountRepository accountRepository;

    private final PhotoRepository photoRepository;

    private final AmazonS3 s3client;

    private final AccountService accountService;

    private final String BUCKET_NAME = "devjeans";

    private final EntityManager entityManager;

    public PhotoService(PhotoRepository photoRepository, AmazonS3 s3client, AccountService accountService, EntityManager entityManager,
                        AccountRepository accountRepository) {
        this.photoRepository = photoRepository;
        this.s3client = s3client;
        this.accountService = accountService;
        this.entityManager = entityManager;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Photo uploadPhoto(MultipartFile image, MultipartFile thumbnail, String photoTitle,Account user) throws IOException {
        log.debug("file upload 시작 : " + image.getOriginalFilename());

        String fileName = image.getOriginalFilename();

        String keyName = LocalDateTime.now() + fileName;
        String thumbnailKeyName = "thumbnail/" + LocalDateTime.now() + fileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getInputStream().available());
        objectMetadata.setContentType("image/jpeg");

        ObjectMetadata thumbnailObjectMetadata = new ObjectMetadata();
        thumbnailObjectMetadata.setContentLength(thumbnail.getInputStream().available());
        thumbnailObjectMetadata.setContentType("image/jpeg");

        s3client.putObject(new PutObjectRequest(BUCKET_NAME, keyName, image.getInputStream(), objectMetadata));
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, thumbnailKeyName, thumbnail.getInputStream(), thumbnailObjectMetadata));

        String photoUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + keyName;
        String thumbnailUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + thumbnailKeyName;

        Photo photo = new Photo(photoUrl, thumbnailUrl, keyName, thumbnailKeyName, photoTitle, user);
        if (photoRepository.findByImageKeyName(keyName).isPresent()) {
            throw new IllegalArgumentException("동일한 사진이 s3 저장소에 존재합니다.");
        }
        user.addPhoto(photo);

        return photoRepository.save(photo);
    }

    public Photo findPhotoById(Long id) {
        return photoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void savePhoto(Photo photo) {
        photoRepository.save(photo);
    }

    @Transactional(readOnly = true)
    public Page<Photo> findAllPhotoOrderByLikeCount(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("likeCount").descending());
        return photoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Photo> findAllPhotoOrderByLatest(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        return photoRepository.findAll(pageable);
    }


    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        Account user = accountService.getAccount(userId);
        Photo photo = findPhotoById(photoId);

        if (Boolean.FALSE.equals(photo.isOwnedBy(user))) {
            throw new RuntimeException("해당 계정은 사진을 삭제할 수 있는 권한이 없습니다.");
        }

        s3client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, photo.getImageKeyName()));
        s3client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, photo.getThumbnailImageKeyName()));

        user.deletePhoto(photo);
        accountRepository.save(user);
        photoRepository.delete(photo);

    }

    @Retryable(value = {StaleStateException.class})
    @Transactional
    public void likePhoto(Photo photo, Account user) {
        if (photo.getUserLiked().contains(user)) {
            throw new IllegalArgumentException("같은 사진에 좋아요를 2번이상 누를 수 없습니다.");
        }
        synchronized (photo) {
            photo.likePhoto(user);
        }
        photoRepository.save(photo);
    }

    @Retryable(value = {StaleStateException.class})
    @Transactional
    public void cancelLikePhoto(Photo photo, Account user) {
        if (!photo.getUserLiked().contains(user)) {
            throw new IllegalArgumentException("좋아요를 누르지 않은 사진에 대해서 좋아요 취소를 할 수 없습니다.");
        }
        synchronized (photo) {
            photo.cancelLikePhoto(user);
        }
        photoRepository.save(photo);
    }
}
