package application.controller;

import application.models.dtos.flashcard.FlashcardDTO;
import application.services.imples.FlashcardServiceImple;
import core.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards")
@Tag(name = "Flashcard Controller", description = "Học từ vựng qua thẻ ghi nhớ (Flashcard)")
public class FlashcardController {

    private final FlashcardServiceImple flashcardService;

    @Operation(
        summary = "Lấy bộ thẻ flashcard theo chủ đề",
        description = "Trả về toàn bộ từ vựng của chủ đề dưới dạng thẻ flashcard. " +
                      "Mỗi thẻ có mặt trước (tiếng Anh + ví dụ) và mặt sau (tiếng Việt). " +
                      "Truyền shuffle=true để trộn ngẫu nhiên thứ tự thẻ."
    )
    @GetMapping
    public ApiResponse<List<FlashcardDTO>> getFlashcards(
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "false") boolean shuffle
    ) {
        return ApiResponse.success(flashcardService.getFlashcards(topicId, shuffle));
    }
}