package application.services.imples;

import application.models.dtos.requests.LoginRequest;
import application.models.dtos.requests.RefreshRequest;
import application.models.dtos.responses.TokenPairResponse;
import application.models.entities.User;
import application.services.AuthService;
import application.services.JwtService;
import core.base.AppException;
import core.constants.ErrorCode;
import core.constants.JwtType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImple implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.access-expiry}")
    private long ACCESS_EXPIRY;

    @Value("${app.jwt.refresh-expiry}")
    private long REFRESH_EXPIRY;

    @Override
    public TokenPairResponse login(LoginRequest request, String deviceId) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();

        assert user != null;

        String accessToken  = jwtService.generateJwt(user.getEmail(), JwtType.ACCESS, ACCESS_EXPIRY);
        String refreshToken = jwtService.generateJwt(user.getEmail(), JwtType.REFRESH, REFRESH_EXPIRY);

        String key = buildKey(authentication.getName(), deviceId);
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_EXPIRY, TimeUnit.SECONDS);

        return new TokenPairResponse(accessToken, refreshToken);
    }

    @Override
    public TokenPairResponse refreshSession(RefreshRequest request, String deviceId) {
        Jwt jwt = jwtService.validateJwt(request.refreshToken());

        String key = buildKey(jwt.getSubject(), deviceId);
        String stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            throw new AppException(ErrorCode.EXPIRED_SESSION);
        }

        if (!stored.equals(request.refreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String newAccess = jwtService.generateJwt(jwt.getSubject(), JwtType.ACCESS, ACCESS_EXPIRY);

        String newRefresh = jwtService.generateJwt(jwt.getSubject(), JwtType.REFRESH, REFRESH_EXPIRY);

        redisTemplate.opsForValue().set(key, newRefresh, REFRESH_EXPIRY, TimeUnit.SECONDS);

        return new TokenPairResponse(newAccess, newRefresh);
    }

    @Override
    public void logout(RefreshRequest rq, String deviceId) {
        Jwt jwt = jwtService.validateJwt(rq.refreshToken());
        String key = buildKey(jwt.getSubject(), deviceId);
        redisTemplate.delete(key);
    }

    @Override
    public void register(String email, String password) {
    //TODO can mailService
    }

    @Override
    public void resetPassword(String email, String newPwd) {
        //TODO can mailService
    }

    private String buildKey(String email, String deviceId) {
        return String.format("%s:%s:%s", JwtType.REFRESH.name(), email, deviceId);
    }
}
