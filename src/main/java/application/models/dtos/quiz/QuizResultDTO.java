package application.models.dtos.quiz;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Kết quả một bài thi.
 * - answers = null khi xem danh sách lịch sử (tối ưu băng thông).
 * - answers = có dữ liệu khi xem chi tiết từng bài.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizResultDTO {

    private Long   resultId;
    private Long   topicId;
    private String topicName;
    private int    score;
    private int    totalQuestion;
    private int    timeTakenSec;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<QuizAnswerDetailDTO> answers;
}