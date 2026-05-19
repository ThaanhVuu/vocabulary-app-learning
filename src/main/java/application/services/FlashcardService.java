package application.services;

import application.models.dtos.flashcard.FlashcardDTO;

import java.util.List;

public interface FlashcardService {

    /**
     * Lấy toàn bộ thẻ flashcard của một chủ đề.
     * Thứ tự trả về là ngẫu nhiên nếu shuffle = true.
     *
     * @param topicId  ID chủ đề
     * @param shuffle  true = trộn ngẫu nhiên, false = theo thứ tự thêm vào
     */
    List<FlashcardDTO> getFlashcards(Long topicId, boolean shuffle);
}