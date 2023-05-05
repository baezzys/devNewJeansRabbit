package com.devJeans.rabbit.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.domain.Report;
import com.devJeans.rabbit.repository.AccountRepository;
import com.devJeans.rabbit.repository.PhotoRepository;
import com.devJeans.rabbit.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PhotoService {

    private static final Log logger = LogFactory.getLog(PhotoService.class);

    private final AccountRepository accountRepository;

    private final PhotoRepository photoRepository;

    private final AmazonS3 s3client;

    private final AccountService accountService;

    private final ReportRepository reportRepository;

    private final String BUCKET_NAME = "devjeans-photo";

    public PhotoService(AccountRepository accountRepository, PhotoRepository photoRepository, AmazonS3 s3client, AccountService accountService, ReportRepository reportRepository) {
        this.accountRepository = accountRepository;
        this.photoRepository = photoRepository;
        this.s3client = s3client;
        this.accountService = accountService;
        this.reportRepository = reportRepository;
    }


    @Transactional
    public Photo uploadPhoto(MultipartFile image, MultipartFile thumbnail, String photoTitle, Account user) throws IOException {
        log.debug("file upload 시작 : " + image.getOriginalFilename());

        String fileName = image.getOriginalFilename();

        // Resize image
        BufferedImage originalImage = ImageIO.read(image.getInputStream());

        // Resize image
        BufferedImage resizedImage = resizeImage(originalImage, 1000, 1000);

        ByteArrayOutputStream resizedImageOutputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", resizedImageOutputStream);
        byte[] resizedImageBytes = resizedImageOutputStream.toByteArray();


        String keyName = LocalDateTime.now() + fileName;
        String thumbnailKeyName = "thumbnail/" + LocalDateTime.now() + fileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedImageBytes.length);
        objectMetadata.setContentType("image/jpeg");

        ObjectMetadata thumbnailObjectMetadata = new ObjectMetadata();
        thumbnailObjectMetadata.setContentLength(thumbnail.getInputStream().available());
        thumbnailObjectMetadata.setContentType("image/jpeg");

        // Upload resized image
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, keyName, new ByteArrayInputStream(resizedImageBytes), objectMetadata));
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

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public Photo findPhotoById(Long id) {
        return photoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void savePhoto(Photo photo) {
        photoRepository.save(photo);
    }

    @Transactional(readOnly = true)
    public Page<Photo> findAllPhotoOrderByLikeCount(int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("likeCount").descending().and(Sort.by("createdDate").descending()));
        return photoRepository.findPhotosWhereIsShowTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Photo> findAllPhotoOrderByLatest(int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("createdDate").descending());
        return photoRepository.findPhotosWhereIsShowTrue(pageable);
    }


    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        Account user = accountService.getAccount(userId);
        Photo photo = findPhotoById(photoId);


        if (Boolean.FALSE.equals(photo.isOwnedBy(user)) && Boolean.FALSE.equals(user.isAdmin())) {
            throw new RuntimeException("해당 계정은 사진을 삭제할 수 있는 권한이 없습니다.");
        }

        s3client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, photo.getImageKeyName()));
        s3client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, photo.getThumbnailImageKeyName()));

        user.deletePhoto(photo);
        accountRepository.save(user);
        photoRepository.delete(photo);

    }

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

    @Transactional
    public void cancelLikePhoto(Photo photo, Account user) {
        if (!photo.getUserLiked().contains(user)) {
            logger.error("user : " + user.getId() + " 는 이미 사진 id : " + photo.getId() + " 에 좋아요를 눌렀습니다.");
            throw new IllegalArgumentException("좋아요를 누르지 않은 사진에 대해서 좋아요 취소를 할 수 없습니다.");
        }
        synchronized (photo) {
            photo.cancelLikePhoto(user);
        }
        photoRepository.save(photo);
    }

    @Transactional
    public void hidePhoto(long photoId) {
        Photo photo = photoRepository.findById(photoId).get();
        photo.hide();
        photoRepository.save(photo);
    }

    @Transactional
    public void showPhoto(long photoId) {
        Photo photo = photoRepository.findById(photoId).get();
        photo.show();
        photoRepository.save(photo);
    }

    public void reportPhoto(Account user, Photo photo, Report.ReportType reportType) {
        if (reportRepository.existsByUserAndPhoto(user, photo)) {
            throw new IllegalStateException("Photo has already been reported by the user");
        }

        Report report = new Report(user, photo, reportType);
        photo.addReport(report);
        user.addReport(report);

        reportRepository.save(report);
    }
}
