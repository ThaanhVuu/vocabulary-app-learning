package application.models.dtos.requests;

import core.base.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class TopicRequest extends BaseDTO{
    @NotBlank(message = "Topic is require")
    private String name;
    private Set<VocabularyDTO> vocabularies;
}
