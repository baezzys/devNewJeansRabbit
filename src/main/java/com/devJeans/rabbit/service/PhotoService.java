package com.devJeans.rabbit.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.devJeans.rabbit.domain.Account;
import com.devJeans.rabbit.domain.Photo;
import com.devJeans.rabbit.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
public class PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    private AmazonS3 s3client;

    public Photo uploadPhoto(MultipartFile file, Account user) throws IOException {
        String fileName = file.getOriginalFilename();
        String bucketName = "dev-jeans";
        String keyName = "photos/" + fileName;

        s3client.putObject(new PutObjectRequest(bucketName, keyName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        String photoUrl = "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
        Photo photo = new Photo(fileName, photoUrl, user);
        user.addPhoto(photo);

        return photoRepository.save(photo);
    }

    public Photo findPhotoById(Long id) {
        return photoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void savePhoto(Photo photo) {
        photoRepository.save(photo);
    }
}
