package com.brundhavanam.user.repository;

import com.brundhavanam.user.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByMobileOrderByIdDesc(String mobile);
}
