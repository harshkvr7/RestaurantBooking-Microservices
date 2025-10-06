package com.rms.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.user_service.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{
    
    VerificationToken findByToken(String Token);
}
