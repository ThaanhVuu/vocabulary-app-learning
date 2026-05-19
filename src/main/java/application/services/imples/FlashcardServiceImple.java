package application.services.imples;

import application.models.dtos.flashcard.FlashcardDTO;
import application.models.entities.Topic;
import application.models.entities.Vocabulary;
import application.repositories.TopicRepository;
import application.services.FlashcardService;
import core.base.AppException;
import core.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImple implements FlashcardService {

    private final TopicRepository topicRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardDTO> getFlashcards(Long topicId, boolean shuffle) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        List<Vocabulary> words = new ArrayList<>(topic.getVocabularies().values());

        if (words.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND,
                    "Chủ đề chưa có từ vựng nào.");
        }

        if (shuffle) {
            Collections.shuffle(words);
        }

        int total = words.size();

        return IntStream.range(0, total)
                .mapToObj(i -> {
                    Vocabulary v = words.get(i);
                    return FlashcardDTO.builder()
                            .wordId(v.getId())
                            .english(v.getEnglish())
                            .vietnamese(v.getVietnamese())
                            .example(v.getExample())
                            .index(i + 1)
                            .total(total)
                            .build();
                })
                .toList();
    }
}