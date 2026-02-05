package com.brundhavanam.auth.service;

import com.brundhavanam.auth.dto.*;

public interface AuthService {

    void sendOtp(String mobile);

    AuthResponse loginWithOtp(OtpVerifyRequest request);
}
