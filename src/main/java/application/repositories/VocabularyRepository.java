package application.repositories;

import application.models.entities.Vocabulary;
import core.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends BaseRepository<Vocabulary, Long> {
}
