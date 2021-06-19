package com.needle.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	protected UUID id;
	
	@Column(name = "content")
    private String content;
    
    @Column(name = "is_visible", columnDefinition = "boolean NOT NULL DEFAULT false")
    private Boolean visible = false;
    
    @Column(name = "make_visible_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime makeVisibleAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp without time zone NOT NULL DEFAULT timezone('utc'::text, now())")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at", columnDefinition = "timestamp without time zone NOT NULL DEFAULT timezone('utc'::text, now())")
    private LocalDateTime modifiedAt;
}
