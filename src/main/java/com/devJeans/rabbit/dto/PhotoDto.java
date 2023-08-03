package com.devJeans.rabbit.dto;

import com.devJeans.rabbit.domain.Photo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PhotoDto {

    private Long photoId;

    private String imageUrl;

    private String thumbnailImageUrl;

    private int visitCount;

    private int likeCount;

    private String photoTitle;

    private AccountDto userDto;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    public PhotoDto(long photoId, String imageUrl, String thumbnailImageUrl, String photoTitle, int visitCount, int likeCount, AccountDto userDto, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.photoId = photoId;
        this.imageUrl = imageUrl;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.photoTitle = photoTitle;
        this.visitCount = visitCount;
        this.likeCount = likeCount;
        this.userDto = userDto;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static PhotoDto of(Photo photo) {
        return new PhotoDto(photo.getId(), photo.getImageUrl(), photo.getThumbnailImageUrl(), photo.getPhotoTitle(), photo.getVisitCount(), photo.getLikeCount(), AccountDto.of(photo.getUserCreated()), photo.getCreatedDate(), photo.getLastModifiedDate());
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public AccountDto getUserDto() {
        return userDto;
    }
}
