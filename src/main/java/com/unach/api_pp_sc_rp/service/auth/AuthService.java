package com.unach.api_pp_sc_rp.service.auth;

import com.unach.api_pp_sc_rp.dto.auth.LoginRequest;
import com.unach.api_pp_sc_rp.dto.auth.PasswordResetRequest;
import com.unach.api_pp_sc_rp.dto.auth.SignupRequest;

public interface AuthService {
    void registerUser(SignupRequest signupRequest);

    LoginRequest.LoginResponse authenticateUser(LoginRequest loginRequest);


    void requestPasswordReset(PasswordResetRequest request);

    void resetPassword(String token, String newPassword);
}
