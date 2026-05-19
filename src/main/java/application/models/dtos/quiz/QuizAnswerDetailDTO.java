package application.models.dtos.quiz;

import lombok.Builder;
import lombok.Getter;

/**
 * Chi tiết 1 câu trả lời khi xem lại bài thi.
 */
@Getter
@Builder
public class QuizAnswerDetailDTO {

    private Long    wordId;
    private String  english;
    private String  userAnswer;
    private String  correctAnswer;
    private boolean isCorrect;
}