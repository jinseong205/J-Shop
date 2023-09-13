package com.shop.server.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;

@Getter
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity{
	
	/* 등록자, 수정자 */ 
    @CreatedBy
    @Column(name ="CRT_ID",updatable = false)
    private String crtName;

    @LastModifiedBy
    @Column(name ="UPDT_ID")
    private String updtName;
}
