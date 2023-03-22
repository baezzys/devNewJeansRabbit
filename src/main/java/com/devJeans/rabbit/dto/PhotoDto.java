package com.devJeans.rabbit.dto;

import com.devJeans.rabbit.domain.Photo;
import lombok.Builder;
import lombok.Getter;

public class PhotoDto {


    private String imageUrl;

    private int visitCount;

    private int likeCount;

    public PhotoDto(String imageUrl, int visitCount, int likeCount) {
        this.imageUrl = imageUrl;
        this.visitCount = visitCount;
        this.likeCount = likeCount;
    }

    public static PhotoDto of(Photo photo) {
        return new PhotoDto(photo.getImageUrl(), photo.getVisitCount(), photo.getLikeCount());
    }
}
