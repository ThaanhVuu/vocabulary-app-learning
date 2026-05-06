package application.models.dtos.requests;

import core.base.BaseDTO;
import jakarta.validation.constraints.NotBlank;

public class VocabularyDTO extends BaseDTO {
    @NotBlank(message = "Vietnamese is require")
    private String vietnamese;

    @NotBlank(message = "English is require")
    private String english;

    private String example;
}
