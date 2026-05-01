package application.services;

import core.constants.JwtType;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {
    String  generateJwt(String subject, JwtType type, long expiry);
    Jwt     validateJwt(String token);
    void    revokeJwt();
    Jwt     decodeToken(String token);
}
