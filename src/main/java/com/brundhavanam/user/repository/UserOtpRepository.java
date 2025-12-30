package com.brundhavanam.user.repository;

import com.brundhavanam.user.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    Optional<UserOtp> findTopByMobileOrderByIdDesc(String mobile);
}
