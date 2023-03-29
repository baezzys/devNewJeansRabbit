package com.devJeans.rabbit.repository;

import com.devJeans.rabbit.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Photo save(Photo photo);
    Optional<Photo> findById(Long photoId);
    Optional<Photo> findByImageKeyName(String iamgeKeyName);
}
