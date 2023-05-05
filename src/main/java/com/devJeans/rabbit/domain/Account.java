package com.devJeans.rabbit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profilePictureUrl;

    @Column(nullable = false)
    private Boolean isBlockedUser = Boolean.FALSE;

    @ManyToMany(mappedBy = "userLiked")
    private Set<Photo> likedPhotos = new HashSet<>();

    @OneToMany(mappedBy = "userCreated", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> createdPhotos = new ArrayList<>();

    @Column(nullable = false)
    private String roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    public List<Report> getReports() {
        return reports;
    }

    public void addReport(Report report) {
        reports.add(report);
        report.setUser(this);
    }


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
        if (createdPhotos.size() >= 29) {
            throw new RuntimeException("사진을 30개이상 추가할 수 없습니다.");
        }
        this.createdPhotos.add(photo);
    }

    public void deletePhoto(Photo photo) {
        photo.getUserLiked().clear();
        photo.setUserCreated(null);
        this.createdPhotos.remove(photo);
    }

    public Boolean isAdmin() {
        if (this.getRoles().equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    public Boolean getBlockedUser() {
        return isBlockedUser;
    }

    public void block() {
        this.isBlockedUser = true;
    }
}
