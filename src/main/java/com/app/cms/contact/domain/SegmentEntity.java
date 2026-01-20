package com.app.cms.contact.domain;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SegmentEntity {

    private String id;
    private String name;
    private String criteria;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
