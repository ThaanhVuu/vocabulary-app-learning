package application.models.dtos.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request nộp bài: chứa toàn bộ câu trả lời + thời gian làm bài.
 */
@Getter
@Setter
public class QuizSubmitRequest {

    @NotNull(message = "topicId là bắt buộc")
    private Long topicId;

    @Valid
    @NotEmpty(message = "Danh sách câu trả lời không được trống")
    private List<SubmitAnswerDTO> answers;

    @Positive(message = "Thời gian làm bài phải lớn hơn 0")
    private int timeTakenSec;
}