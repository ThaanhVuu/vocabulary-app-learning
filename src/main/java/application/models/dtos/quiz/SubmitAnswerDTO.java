package application.models.dtos.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Câu trả lời của người dùng cho 1 câu hỏi trong bài thi.
 */
@Getter
@Setter
public class SubmitAnswerDTO {

    @NotNull(message = "wordId là bắt buộc")
    private Long wordId;

    @NotBlank(message = "userAnswer là bắt buộc")
    private String userAnswer;

    @NotBlank(message = "correctAnswer là bắt buộc")
    private String correctAnswer;
}