package application.mapper;

import application.models.dtos.requests.TopicRequest;
import application.models.entities.Topic;
import core.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {VocabularyMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TopicMapper extends BaseMapper<Topic, TopicRequest> {

}
