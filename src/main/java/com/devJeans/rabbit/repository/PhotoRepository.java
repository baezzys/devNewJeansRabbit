package com.devJeans.rabbit.repository;

import com.devJeans.rabbit.domain.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Photo save(Photo photo);
    Optional<Photo> findById(Long photoId);
    Optional<Photo> findByImageKeyName(String iamgeKeyName);

    @Query("SELECT p FROM Photo p WHERE p.isShow = true")
    Page<Photo> findPhotosWhereIsShowTrue(Pageable pageable);

    @Query("SELECT p FROM Photo p WHERE p.isShow = true AND p.createdDate > :oneWeekAgo")
    Page<Photo> findPhotosWhereIsShowTrueAndCreatedDateAfter(Pageable pageable, @Param("oneWeekAgo") LocalDateTime oneWeekAgo);
}
