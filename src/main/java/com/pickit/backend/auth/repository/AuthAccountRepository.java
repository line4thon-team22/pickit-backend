package com.pickit.backend.auth.repository;

import com.pickit.backend.auth.entity.AuthAccount;
import com.pickit.backend.auth.entity.AuthProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {
    Optional<AuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
    boolean existsByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
