package com.devJeans.rabbit.repository;

import com.devJeans.rabbit.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findByImageKeyName(String iamgeKeyName);
}
