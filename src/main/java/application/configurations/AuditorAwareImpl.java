package application.configurations;

import core.constants.Constants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            return Optional.of(0L);
        }

        // ✅ principal là Jwt
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.of(0L);
        }

        Long userId = jwt.getClaim(Constants.SUBJECT_ID.name());
        return Optional.ofNullable(userId).or(() -> Optional.of(0L));
    }
}