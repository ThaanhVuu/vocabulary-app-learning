package application.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class RSAProperties {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private String kid = UUID.randomUUID().toString();
}
