package application.services.imples;

import application.mapper.TopicMapper;
import application.models.dtos.requests.TopicRequest;
import application.models.entities.Topic;
import application.models.entities.User;
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
import core.base.FilterRequest;
import core.base.SearchRequest;
import core.constants.ErrorCode;
import core.constants.Operator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TopicServiceImple extends BaseService<Topic, TopicRequest, Long, TopicRepository> implements TopicService {
    @Value("${GEMINI_API_KEY}")
    private String API_KEY;

    public TopicServiceImple(TopicRepository repository, TopicMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void beforeCreate(TopicRequest dto, Topic entity) {
        User userRef = SecurityUtils.getCurrentUser();
        entity.setUser(userRef);
        super.beforeCreate(dto, entity);
    }

    @Override
    @Transactional
    protected void beforeSearch(SearchRequest request) {
        FilterRequest filterByUserId = new FilterRequest();
        filterByUserId.setField("user.id");
        filterByUserId.setOperator(Operator.EQ);
        filterByUserId.setValue(SecurityUtils.getCurrentUserId());

        request.getFilters().add(filterByUserId);
        super.beforeSearch(request);
    }

    @Override
    public Set<Vocabulary> generateVocabulariesByGeminiAndSaveWithTopic(TopicRequest request) {

        Client client = Client.builder()
                .apiKey(API_KEY)
                .build();

        String prompt = """
                Bạn là một chuyên gia ngôn ngữ và giáo dục. Hãy tạo danh sách 20 từ vựng tiếng Anh phổ biến và hữu ích nhất thuộc chủ đề: %s.
                
                Yêu cầu BẮT BUỘC:
                
                1. Trả về kết quả DUY NHẤT dưới dạng một mảng JSON hợp lệ.
                
                2. KHÔNG thêm bất kỳ lời chào, văn bản giải thích hay dấu markdown (như ```json) nào bao quanh kết quả. Chỉ trả về chuỗi JSON thuần túy để hệ thống đọc.
                
                3. Mỗi phần tử trong mảng phải tuân thủ chính xác cấu trúc sau:
                {
                "english": "từ vựng tiếng Anh",
                "vietnamese": "nghĩa tiếng Việt",
                "example": "một câu ví dụ tiếng Anh ngắn gọn và dễ hiểu chứa từ này"
                }
                """.formatted(request.getName());

        GenerateContentResponse response =
                client.models.generateContent("gemini-3-flash-preview", prompt, null);

        client.close();

        Set<Vocabulary> vocabs = parseJsonToVocabs(response.text());

        Topic topic = repository.getTopicByName(request.getName())
                .orElse(new Topic(request.getName(), vocabs.size(), SecurityUtils.getCurrentUser(), vocabs));
        vocabs.forEach(vocab -> vocab.setTopic(topic));
        return repository.save(topic).getVocabularies();
    }

    private Set<Vocabulary> parseJsonToVocabs(String rawJson) {
        Set<Vocabulary> vocabularies = new HashSet<>();
        try {
            String cleanJson = rawJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            ObjectMapper mapper = new ObjectMapper();
            vocabularies.addAll(mapper.readValue(
                    cleanJson,
                    new TypeReference<List<Vocabulary>>() {
                    }
            ));
        } catch (Exception e) {
            log.error("Lỗi khi parse Vocab: ", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return vocabularies;
    }
}
