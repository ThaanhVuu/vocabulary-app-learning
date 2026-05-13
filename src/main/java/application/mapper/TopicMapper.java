package application.mapper;

import application.models.dtos.requests.TopicRequest;
import application.models.entities.Topic;
import core.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TopicMapper implements BaseMapper<Topic, TopicRequest> {

    @Autowired
    protected VocabularyMapper vocabularyMapper;

    @Override
    public TopicRequest toDto(Topic entity) {
        if (entity == null) return null;

        TopicRequest dto = new TopicRequest();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIsDeleted(entity.isDeleted());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getModifiedAt());

        // Chuyển đổi Map<String, Vocabulary> sang Set<VocabularyDTO>
        if (entity.getVocabularies() != null) {
            dto.setVocabularies(vocabularyMapper.mapEntityMapToDtoSet(entity.getVocabularies()));
        }

        return dto;
    }

    @Override
    public Topic toEntity(TopicRequest dto) {
        if (dto == null) return null;

        Topic topic = Topic.builder()
                .name(dto.getName())
                .vocabularies(new HashMap<>())
                .build();

        if (dto.getId() != null) topic.setId(dto.getId());
        topic.setDeleted(dto.getIsDeleted());

        if (dto.getVocabularies() != null) {
            dto.getVocabularies().forEach(vDto -> {
                topic.addVocabulary(vocabularyMapper.toEntity(vDto));
            });
        }

        return topic;
    }
}