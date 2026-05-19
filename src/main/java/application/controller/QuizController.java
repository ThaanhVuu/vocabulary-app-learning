package application.controller;

import application.models.dtos.quiz.QuizQuestionDTO;
import application.models.dtos.quiz.QuizResultDTO;
import application.models.dtos.quiz.QuizSubmitRequest;
import application.services.imples.QuizServiceImple;
import core.base.ApiResponse;
import core.base.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quiz")
@Tag(name = "Quiz Controller", description = "Tạo đề thi, nộp bài và xem kết quả trắc nghiệm")
public class QuizController {

    private final QuizServiceImple quizService;

    @Operation(
        summary = "Tạo bộ câu hỏi trắc nghiệm",
        description = "Lấy ngẫu nhiên N từ vựng trong chủ đề. " +
                      "Mỗi câu có 4 lựa chọn (1 đúng + 3 nhiễu). " +
                      "Mặc định 10 câu, tối đa bằng số từ của chủ đề."
    )
    @GetMapping("/generate")
    public ApiResponse<List<QuizQuestionDTO>> generateQuiz(
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "10") int questionCount
    ) {
        return ApiResponse.success(quizService.generateQuiz(topicId, questionCount));
    }

    @Operation(
        summary = "Nộp bài kiểm tra",
        description = "Gửi danh sách câu trả lời. Hệ thống tự chấm điểm và lưu kết quả. " +
                      "Trả về điểm số và chi tiết đúng/sai từng câu."
    )
    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QuizResultDTO> submitQuiz(@Valid @RequestBody QuizSubmitRequest request) {
        return ApiResponse.success(quizService.submitQuiz(request));
    }

    @Operation(
        summary = "Lịch sử thi của tôi",
        description = "Danh sách các bài đã thi (phân trang), sắp xếp theo thời gian mới nhất. " +
                      "Không bao gồm chi tiết câu trả lời."
    )
    @GetMapping("/history")
    public ApiResponse<PageResponse<QuizResultDTO>> getHistory(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(quizService.getMyHistory(page, size));
    }

    @Operation(
        summary = "Chi tiết một bài thi",
        description = "Xem lại đáp án đúng/sai từng câu của một bài đã nộp. " +
                      "Chỉ được xem bài của chính mình."
    )
    @GetMapping("/history/{resultId}")
    public ApiResponse<QuizResultDTO> getResultDetail(@PathVariable Long resultId) {
        return ApiResponse.success(quizService.getResultDetail(resultId));
    }
}