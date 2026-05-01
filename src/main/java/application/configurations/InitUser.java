package application.configurations;

import application.models.entities.User;
import application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InitUser {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        final String adminEmail = "test@test.com";
        final String pwd = "12345678";

        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .email(adminEmail)
                        .password(encoder.encode(pwd))
                        .enabled(true)
                        .build();

                userRepository.save(admin);
                log.info(">>> Khởi tạo thành công tài khoản test: {}", adminEmail);
            } else {
                log.info(">>> Tài khoản test đã tồn tại.");
            }
        };
    }
}
