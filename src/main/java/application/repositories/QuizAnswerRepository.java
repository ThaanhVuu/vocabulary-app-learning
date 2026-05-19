package application.repositories;

import application.models.entities.QuizAnswer;
import core.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepository extends BaseRepository<QuizAnswer, Long> {
}