package com.devJeans.rabbit.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.repository.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;

    private final AmazonS3 s3client;

    private final AccountService accountService;

    private final String BUCKET_NAME = "devjeans";

    public PhotoService(PhotoRepository photoRepository, AmazonS3 s3client, AccountService accountService) {
        this.photoRepository = photoRepository;
        this.s3client = s3client;
        this.accountService = accountService;
    }

    @Transactional
    public Photo uploadPhoto(MultipartFile file, Account user) throws IOException {
        log.debug("file upload 시작 : " + file.getOriginalFilename());

        String fileName = file.getOriginalFilename();
        String keyName = fileName + LocalDateTime.now();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getInputStream().available());
        objectMetadata.setContentType("image/jpeg");

        s3client.putObject(new PutObjectRequest(BUCKET_NAME, keyName, file.getInputStream(), objectMetadata));

        String photoUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + keyName;
        Photo photo = new Photo(photoUrl, keyName, user);
        if (photoRepository.findByFileName(keyName).isPresent()) {
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
    public Page<Photo> findAllPhoto(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("likeCount").descending());
        return photoRepository.findAll(pageable);
    }

    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        Account user = accountService.getAccount(userId);
        Photo photo = findPhotoById(photoId);

        if (Boolean.FALSE.equals(photo.isOwnedBy(user))) {
            throw new RuntimeException("해당 계정은 사진을 삭제할 수 있는 권한이 없습니다.");
        }

        s3client.deleteObject(BUCKET_NAME, photo.getFileName());
        user.deletePhoto(photo);
    }
}
