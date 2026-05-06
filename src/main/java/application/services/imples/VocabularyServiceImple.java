package application.services.imples;

import application.mapper.VocabularyMapper;
import application.models.dtos.requests.VocabularyDTO;
import application.models.entities.Vocabulary;
import application.repositories.VocabularyRepository;
import core.base.BaseService;

public class VocabularyServiceImple extends BaseService<Vocabulary, VocabularyDTO, Long, VocabularyRepository>{
    public VocabularyServiceImple(VocabularyRepository repository, VocabularyMapper mapper) {
        super(repository, mapper);
    }


}
