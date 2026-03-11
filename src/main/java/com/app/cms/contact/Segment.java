package com.app.cms.contact;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

public class Segment {
    private String id;
    private Long organizationId;
    private String name;
    private String criteria;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
