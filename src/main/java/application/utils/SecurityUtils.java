package application.utils;

import application.models.entities.User;
import application.repositories.UserRepository;
import core.base.AppException;
import core.constants.ErrorCode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurityUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            throw new AppException(ErrorCode.UNAUTH);
        }

        // ✅ kiểm tra null và đúng type trước khi cast
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTH);
        }

        String email = jwt.getSubject();
        if (email == null) {
            throw new AppException(ErrorCode.UNAUTH);
        }

        UserRepository userRepository = context.getBean(UserRepository.class);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTH));
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}