package com.devJeans.rabbit.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Photo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String thumbnailImageUrl;

    @Column(nullable = false)
    private String imageKeyName;

    @Column(nullable = false)
    private String thumbnailImageKeyName;

    @Column(nullable = false)
    private String photoTitle;

    @ManyToOne
    private Account user;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int visitCount = 0;

    public Photo() {

    }

    public Photo(String imageUrl, String thumbnailUrl, String keyName, String thumbnailImageKeyName, String photoTitle, Account user) {
        this.imageUrl = imageUrl;
        this.thumbnailImageUrl = thumbnailUrl;
        this.imageKeyName = keyName;
        this.thumbnailImageKeyName = thumbnailImageKeyName;
        this.photoTitle = photoTitle;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void likePhoto() {
        this.likeCount++;
    }

    public void cancelLikePhoto() {
        if (likeCount == 0) {
            throw new IllegalStateException("좋아요는 음수가 될 수 없습니다.");
        }
        this.likeCount--;
    }

    public void addVisitCount() {
        this.visitCount++;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isOwnedBy(Account user) {
        return this.user.equals(user);
    }

    public String getImageKeyName() {
        return imageKeyName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Photo)) {
            return false;
        }
        Photo other = (Photo) obj;
        return Objects.equals(imageUrl, other.getImageUrl()) &&
                Objects.equals(imageKeyName, other.getImageKeyName()) &&
                likeCount == other.likeCount &&
                visitCount == other.visitCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl, imageKeyName, likeCount, visitCount);
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public String getThumbnailImageKeyName() {
        return thumbnailImageKeyName;
    }
}
