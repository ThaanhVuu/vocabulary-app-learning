package application;

import core.base.BaseRepositoryImple;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(
        basePackages = "application.repositories",
        repositoryBaseClass = BaseRepositoryImple.class
)
public class VocabularyLearningAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(VocabularyLearningAppApplication.class, args);
    }
}
