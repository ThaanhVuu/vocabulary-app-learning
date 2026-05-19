package application.models.dtos.flashcard;

import lombok.Builder;
import lombok.Getter;

/**
 * Một thẻ flashcard trả về cho client.
 * Client hiển thị mặt trước (english), người dùng đoán rồi lật để xem mặt sau (vietnamese).
 */
@Getter
@Builder
public class FlashcardDTO {

    private Long   wordId;
    private String english;
    private String vietnamese;
    private String example;
    private int    index;     // Vị trí thẻ trong bộ (1-based), để hiển thị "Thẻ 3/20"
    private int    total;     // Tổng số thẻ trong bộ
}