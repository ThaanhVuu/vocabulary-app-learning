package application.services;

import application.models.dtos.requests.LoginRequest;
import application.models.dtos.requests.RefreshRequest;
import application.models.dtos.responses.TokenPairResponse;

public interface AuthService {
    TokenPairResponse   login(LoginRequest request, String deviceId);
    TokenPairResponse   refreshSession(RefreshRequest request, String deviceId);
    void                logout(RefreshRequest request, String devideId);
    void                register(String email, String password);
    void                resetPassword(String email, String newPwd);
}
