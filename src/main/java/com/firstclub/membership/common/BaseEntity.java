package com.firstclub.membership.common;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Shared identity, optimistic-lock version, and audit timestamps for all entities.
 * {@code @Version} gives every aggregate optimistic locking for free — concurrent
 * edits to the same row fail fast instead of silently overwriting.
 * No {@code equals}/{@code hashCode} here: identity-based equality on mutable JPA
 * entities is a known footgun, so we leave it to default reference equality.
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
