package application.models.dtos.responses;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {
}
