package com.devJeans.rabbit.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // the key of the photo file stored in S3

    @Column(nullable = false)
    private String fileName;

    @ManyToOne
    private Account user;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int visitCount = 0;

    public Photo() {

    }

    public Photo(String imageUrl, String fileName, Account user) {
        this.imageUrl = imageUrl;
        this.fileName = fileName;
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

    public String getFileName() {
        return fileName;
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
                Objects.equals(fileName, other.getFileName()) &&
                likeCount == other.likeCount &&
                visitCount == other.visitCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl, fileName, likeCount, visitCount);
    }
}
