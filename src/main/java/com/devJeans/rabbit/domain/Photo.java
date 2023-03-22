package com.devJeans.rabbit.domain;

import javax.persistence.*;

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

    public void cancleLikePhoto() {
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
}
