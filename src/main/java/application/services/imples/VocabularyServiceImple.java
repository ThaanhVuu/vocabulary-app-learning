package application.services.imples;

import application.exception.ErrorCode;
import application.mapper.VocabularyMapper;
import application.models.dtos.requests.VocabularyDTO;
import application.models.entities.Topic;
import application.models.entities.Vocabulary;
import application.repositories.VocabularyRepository;
import core.base.AppException;
import core.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class VocabularyServiceImple extends BaseService<Vocabulary, VocabularyDTO, Long, VocabularyRepository>{
    public VocabularyServiceImple(VocabularyRepository repository, VocabularyMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void beforeCreate(VocabularyDTO dto, Vocabulary entity) {
        if(dto.getTopicId() == null){
            throw new AppException(ErrorCode.TOPIC_REQUIRE);
        }
    }
}
