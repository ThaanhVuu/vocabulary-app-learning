package application.repositories;

import application.models.entities.Topic;
import core.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends BaseRepository<Topic, Long> {
    Optional<Topic> getTopicByName(String name);
}
