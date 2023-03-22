package com.devJeans.rabbit.repository;

import com.devJeans.rabbit.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
