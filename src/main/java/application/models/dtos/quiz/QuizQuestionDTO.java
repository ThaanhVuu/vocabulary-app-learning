package application.models.dtos.quiz;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Một câu hỏi trắc nghiệm trả về cho client.
 * Hiển thị từ tiếng Anh + ví dụ, client đoán nghĩa tiếng Việt từ 4 lựa chọn.
 */
@Getter
@Builder
public class QuizQuestionDTO {

    private Long         wordId;
    private String       english;
    private String       example;
    private List<String> options; // 4 đáp án (đã trộn ngẫu nhiên, 1 đúng + 3 nhiễu)
}