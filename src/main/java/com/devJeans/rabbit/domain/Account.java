package com.devJeans.rabbit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.server.MethodNotAllowedException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePictureUrl;

    @OneToMany
    private Set<Photo> likedPhotos = new HashSet<>();

    @OneToMany(mappedBy = "userCreated")
    private List<Photo> createdPhotos = new ArrayList<>();

    private String roles;

    public Account(Long id, String firstName, String lastName, String email, String pictureUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePictureUrl = pictureUrl;
    }

    public Account(String firstName, String lastName, String email, String pictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePictureUrl = pictureUrl;
    }

    public void addPhoto(Photo photo) {
        if (createdPhotos.size() >= 6) {
            throw new RuntimeException("사진을 7개이상 추가할 수 없습니다.");
        }
        this.createdPhotos.add(photo);
    }

    public void deletePhoto(Photo photo) {
        if (!this.createdPhotos.contains(photo)) {
            throw new RuntimeException("해당 사진을 삭제할 수 없습니다.");
        }
        this.createdPhotos.remove(photo);
    }

    public void addLikedPhoto(Photo photo) {
        if (!likedPhotos.contains(photo)) {
            likedPhotos.add(photo);
        }
    }

    public synchronized void removeLikedPhoto(Photo photo) {
        if (likedPhotos.contains(photo)) {
            likedPhotos.remove(photo);
        }
    }
}
