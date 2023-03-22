package com.devJeans.rabbit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.server.MethodNotAllowedException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePictureUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    private String roles;

    public Account(String firstName, String lastName, String email, String pictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePictureUrl = pictureUrl;
    }

    public void addPhoto(Photo photo) {
        if (photos.size() > 5) {
            throw new RuntimeException("사진을 5개이상 추가할 수 없습니다.");
        }
        this.photos.add(photo);
    }
}
