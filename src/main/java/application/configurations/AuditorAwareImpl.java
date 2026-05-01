package application.configurations;

import application.models.entities.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            return Optional.of(0L);
        }

        User user = (User) authentication.getPrincipal();

        assert user != null;
        return Optional.of(user.getId());
    }
}