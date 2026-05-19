package application.services.imples;

import application.models.dtos.quiz.*;
import application.models.entities.*;
import application.repositories.QuizResultRepository;
import application.repositories.TopicRepository;
import application.repositories.VocabularyRepository;
import application.services.QuizService;
import application.utils.SecurityUtils;
import core.base.AppException;
import core.base.PageResponse;
import core.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImple implements QuizService {

    private final TopicRepository      topicRepository;
    private final VocabularyRepository vocabularyRepository;
    private final QuizResultRepository quizResultRepository;

    // ─────────────────────────────────────────────────────────────
    // 1. TẠO ĐỀ THI
    // ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionDTO> generateQuiz(Long topicId, int questionCount) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        List<Vocabulary> allWords = new ArrayList<>(topic.getVocabularies().values());

        if (allWords.size() < 4) {
            throw new AppException(ErrorCode.INVALID_PARAM,
                    "Chủ đề cần ít nhất 4 từ vựng để tạo bài kiểm tra.");
        }

        // Trộn và giới hạn số câu
        Collections.shuffle(allWords);
        int actual = Math.min(questionCount, allWords.size());
        List<Vocabulary> selected = allWords.subList(0, actual);

        return selected.stream()
                .map(word -> buildQuestion(word, allWords))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // 2. NỘP BÀI
    // ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public QuizResultDTO submitQuiz(QuizSubmitRequest request) {
        User  currentUser = SecurityUtils.getCurrentUser();
        Topic topic       = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        int correctCount = 0;
        List<QuizAnswer> answerEntities = new ArrayList<>();

        for (SubmitAnswerDTO dto : request.getAnswers()) {
            Vocabulary vocab = vocabularyRepository.findById(dto.getWordId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            boolean isCorrect = dto.getCorrectAnswer().trim()
                    .equalsIgnoreCase(dto.getUserAnswer().trim());

            if (isCorrect) correctCount++;

            answerEntities.add(QuizAnswer.builder()
                    .vocabulary(vocab)
                    .userAnswer(dto.getUserAnswer())
                    .correctAnswer(dto.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        QuizResult result = QuizResult.builder()
                .user(currentUser)
                .topic(topic)
                .score(correctCount)
                .totalQuestion(request.getAnswers().size())
                .timeTakenSec(request.getTimeTakenSec())
                .build();

        // Liên kết answers → result rồi lưu 1 lần (cascade ALL)
        answerEntities.forEach(a -> {
            a.setQuizResult(result);
            result.getAnswers().add(a);
        });

        QuizResult saved = quizResultRepository.save(result);
        return toDetailDTO(saved);
    }

    // ─────────────────────────────────────────────────────────────
    // 3. LỊCH SỬ THI
    // ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PageResponse<QuizResultDTO> getMyHistory(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<QuizResult> resultPage = quizResultRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        return PageResponse.<QuizResultDTO>builder()
                .items(resultPage.getContent().stream()
                        .map(this::toSummaryDTO)
                        .collect(Collectors.toList()))
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // 4. CHI TIẾT BÀI THI
    // ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public QuizResultDTO getResultDetail(Long resultId) {
        Long userId = SecurityUtils.getCurrentUserId();

        QuizResult result = quizResultRepository.findByIdAndUserId(resultId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        return toDetailDTO(result);
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    /**
     * Tạo 1 câu hỏi: lấy 3 đáp án nhiễu từ allWords (khác target), thêm đáp án đúng, trộn ngẫu nhiên.
     */
    private QuizQuestionDTO buildQuestion(Vocabulary target, List<Vocabulary> allWords) {
        List<String> distractors = allWords.stream()
                .filter(v -> !v.getId().equals(target.getId()))
                .map(Vocabulary::getVietnamese)
                .collect(Collectors.toList());

        Collections.shuffle(distractors);

        List<String> options = new ArrayList<>(distractors.subList(0, 3));
        options.add(target.getVietnamese());
        Collections.shuffle(options);

        return QuizQuestionDTO.builder()
                .wordId(target.getId())
                .english(target.getEnglish())
                .example(target.getExample())
                .options(options)
                .build();
    }

    /** Dùng cho danh sách lịch sử: không bao gồm chi tiết câu trả lời. */
    private QuizResultDTO toSummaryDTO(QuizResult r) {
        return QuizResultDTO.builder()
                .resultId(r.getId())
                .topicId(r.getTopic().getId())
                .topicName(r.getTopic().getName())
                .score(r.getScore())
                .totalQuestion(r.getTotalQuestion())
                .timeTakenSec(r.getTimeTakenSec())
                .createdAt(r.getCreatedAt())
                .build();
    }

    /** Dùng cho chi tiết bài thi: bao gồm đáp án từng câu. */
    private QuizResultDTO toDetailDTO(QuizResult r) {
        List<QuizAnswerDetailDTO> details = r.getAnswers().stream()
                .map(a -> QuizAnswerDetailDTO.builder()
                        .wordId(a.getVocabulary().getId())
                        .english(a.getVocabulary().getEnglish())
                        .userAnswer(a.getUserAnswer())
                        .correctAnswer(a.getCorrectAnswer())
                        .isCorrect(a.isCorrect())
                        .build())
                .collect(Collectors.toList());

        return QuizResultDTO.builder()
                .resultId(r.getId())
                .topicId(r.getTopic().getId())
                .topicName(r.getTopic().getName())
                .score(r.getScore())
                .totalQuestion(r.getTotalQuestion())
                .timeTakenSec(r.getTimeTakenSec())
                .createdAt(r.getCreatedAt())
                .answers(details)
                .build();
    }
}