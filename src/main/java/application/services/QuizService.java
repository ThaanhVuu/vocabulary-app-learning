package application.services;

import application.models.dtos.quiz.QuizQuestionDTO;
import application.models.dtos.quiz.QuizResultDTO;
import application.models.dtos.quiz.QuizSubmitRequest;
import core.base.PageResponse;

import java.util.List;

public interface QuizService {

    /**
     * Tạo bộ câu hỏi trắc nghiệm ngẫu nhiên từ một chủ đề.
     * Mỗi câu: 1 từ tiếng Anh → đoán nghĩa tiếng Việt, 4 lựa chọn.
     *
     * @param topicId       ID chủ đề
     * @param questionCount Số câu hỏi muốn lấy (tối đa bằng số từ của chủ đề)
     */
    List<QuizQuestionDTO> generateQuiz(Long topicId, int questionCount);

    /**
     * Nộp bài và lưu kết quả.
     * Trả về QuizResultDTO kèm chi tiết đúng/sai từng câu.
     */
    QuizResultDTO submitQuiz(QuizSubmitRequest request);

    /**
     * Lịch sử thi của người dùng hiện tại (phân trang, không có chi tiết câu trả lời).
     */
    PageResponse<QuizResultDTO> getMyHistory(int page, int size);

    /**
     * Chi tiết một bài thi (kèm đáp án từng câu).
     * Chỉ được xem bài của chính mình.
     */
    QuizResultDTO getResultDetail(Long resultId);
}