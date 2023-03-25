package com.devJeans.rabbit.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@Accessors(chain = true)
@EntityListeners(value = { AuditingEntityListener.class })
public abstract class BaseEntity implements Serializable {

    @CreatedDate
    @Column(name = "created_date", updatable = false) //한번 생성된 날짜는 수정하지 않는다.
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

}
