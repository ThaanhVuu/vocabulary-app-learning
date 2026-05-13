package core.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseDTO {

    @Schema(description = "Auto-incremented ID, not required for creation", accessMode = Schema.AccessMode.READ_ONLY)
    private Long             id;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime   createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime   updatedAt;

    private Boolean         isDeleted;
}