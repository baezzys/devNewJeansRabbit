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

@Service
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;

    private final AmazonS3 s3client;

    private final AccountService accountService;

    public PhotoService(PhotoRepository photoRepository, AmazonS3 s3client, AccountService accountService) {
        this.photoRepository = photoRepository;
        this.s3client = s3client;
        this.accountService = accountService;
    }

    @Transactional
    public Photo uploadPhoto(MultipartFile file, Account user) throws IOException {
        log.debug("file upload 시작 : " + file.getOriginalFilename());

        String fileName = file.getOriginalFilename();
        String bucketName = "devjeans";
        String keyName = fileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getInputStream().available());
        objectMetadata.setContentType("image/jpeg");

        s3client.putObject(new PutObjectRequest(bucketName, keyName, file.getInputStream(), objectMetadata));

        String photoUrl = "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
        Photo photo = new Photo(photoUrl, fileName, user);
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

    public void deletePhoto(Long userId, Long photoId) {
        Account user = accountService.getAccount(userId);
        Photo photo = findPhotoById(photoId);

        if (Boolean.FALSE.equals(photo.isOwnedBy(user))) {
            throw new RuntimeException("해당 계정은 사진을 삭제할 수 있는 권한이 없습니다.");
        }

        user.deletePhoto(photo);
    }
}
