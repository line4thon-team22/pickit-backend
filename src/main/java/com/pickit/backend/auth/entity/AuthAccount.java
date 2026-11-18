package com.pickit.backend.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "auth_accounts")
@EntityListeners(AuditingEntityListener.class)
public class AuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_account_id")
    private Long authAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, unique = true)
    private String providerUserId;

    @Column(name = "password_hash")
    private String passwordHash;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected AuthAccount() {
    }

    public AuthAccount(User user, AuthProvider provider, String providerUserId, String passwordHash) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.passwordHash = passwordHash;
    }

    public Long getAuthAccountId() {
        return authAccountId;
    }

    public User getUser() {
        return user;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
