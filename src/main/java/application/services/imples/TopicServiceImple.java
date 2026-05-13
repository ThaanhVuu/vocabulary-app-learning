package application.services.imples;

import application.mapper.TopicMapper;
import application.mapper.VocabularyMapper;
import application.models.dtos.requests.TopicRequest;
import application.models.dtos.requests.VocabularyDTO;
import application.models.entities.Topic;
import application.models.entities.Vocabulary;
import application.repositories.TopicRepository;
import application.services.TopicService;
import application.utils.SecurityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import core.base.AppException;
import core.base.BaseService;
import core.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class TopicServiceImple extends BaseService<Topic, TopicRequest, Long, TopicRepository> implements TopicService {

    @Value("${GEMINI_API_KEY}")
    private String API_KEY;

    private final VocabularyMapper vocabularyMapper;

    public TopicServiceImple(TopicRepository repository, TopicMapper mapper, VocabularyMapper vocabularyMapper) {
        super(repository, mapper);
        this.vocabularyMapper = vocabularyMapper;
    }

    @Override
    protected void beforeCreate(TopicRequest dto, Topic entity) {
        entity.setUser(SecurityUtils.getCurrentUser());
        entity.setWordCount(entity.getVocabularies().size());
    }

    @Override
    @Transactional
    public Set<Vocabulary> generateVocabulariesByGeminiAndSaveWithTopic(TopicRequest request) {
        Client client = Client.builder().apiKey(API_KEY).build();

        String prompt = "Tạo 20 từ vựng JSON cho chủ đề: " + request.getName() +
                ". Định dạng: [{\"english\": \"...\", \"vietnamese\": \"...\", \"example\": \"...\"}]";

        GenerateContentResponse response = client.models.generateContent("gemini-3-flash-preview", prompt, null);
        client.close();

        Set<Vocabulary> vocabs = parseJsonToVocabs(response.text());

        // Tìm topic cũ theo name và user hiện tại (tránh ghi đè topic của người khác)
        Topic topic = repository.getTopicByName(request.getName())
                .orElseGet(() -> Topic.builder()
                        .name(request.getName())
                        .user(SecurityUtils.getCurrentUser())
                        .build());

        vocabs.forEach(topic::addVocabulary);
        topic.setWordCount(topic.getVocabularies().size());

        return new HashSet<>(repository.save(topic).getVocabularies().values());
    }

    @Transactional
    public TopicRequest removeVocabs(Long topicId, Set<String> englishWords) {
        return execute(() -> {
            Topic topic = get(topicId);

            // Xóa theo key english trong Map
            englishWords.forEach(word -> topic.getVocabularies().remove(word));
            topic.setWordCount(topic.getVocabularies().size());

            return mapper.toDto(repository.save(topic));
        });
    }

    @Transactional
    public TopicRequest updateVocabs(Long topicId, Set<VocabularyDTO> vocabDtos) {
        return execute(() -> {
            Topic topic = get(topicId);
            Map<String, Vocabulary> currentMap = topic.getVocabularies();

            for (VocabularyDTO dto : vocabDtos) {
                Vocabulary existing = currentMap.get(dto.getEnglish());
                if (existing != null) {
                    // Update nội dung cho từ đã có
                    vocabularyMapper.updateEntityFromDto(dto, existing);
                } else {
                    // Thêm từ hoàn toàn mới vào Map
                    topic.addVocabulary(vocabularyMapper.toEntity(dto));
                }
            }
            topic.setWordCount(topic.getVocabularies().size());

            return mapper.toDto(repository.save(topic));
        });
    }

    private Set<Vocabulary> parseJsonToVocabs(String rawJson) {
        try {
            // Loại bỏ markdown nếu có
            String cleanJson = rawJson.replaceAll("(?s)```(json)?\\s*", "").replace("```", "").trim();
                    ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(cleanJson, new TypeReference<Set<Vocabulary>>() {});
        } catch (Exception e) {
            log.error("Parse Error JSON từ Gemini: {}", rawJson, e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}