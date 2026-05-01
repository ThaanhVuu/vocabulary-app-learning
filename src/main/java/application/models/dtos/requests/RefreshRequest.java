package application.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest (
        @NotBlank String refreshToken
){
}
