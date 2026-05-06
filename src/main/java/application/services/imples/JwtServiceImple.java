package application.services.imples;

import application.services.JwtService;
import core.base.AppException;
import core.constants.Constants;
import core.constants.ErrorCode;
import core.constants.JwtType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImple implements JwtService {
    private final JwtDecoder jwtDecoder;
    private final JwtEncoder jwtEncoder;
    @Value("${app.jwt.issuer}")
    private String ISSUER;

    @Override
    public String generateJwt(
            String  subject,
            long subjectId,
            JwtType type,
            long expiry
    ) {
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(ISSUER)
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(expiry, ChronoUnit.SECONDS))
                .claim(Constants.TOKEN_TYPE.name(), type)
                .claim(Constants.SUBJECT_ID.name(), subjectId)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @Override
    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }

    @Override
    public Jwt validateJwt(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        if (!ISSUER.equals(jwt.getClaimAsString("iss"))) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (!JwtType.REFRESH.name().equals(jwt.getClaimAsString(Constants.TOKEN_TYPE.name()))) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return jwt;
    }

    @Override
    public void revokeJwt() {

    }
}
