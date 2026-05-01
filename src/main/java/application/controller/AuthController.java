package application.controller;

import application.models.dtos.requests.LoginRequest;
import application.models.dtos.requests.RefreshRequest;
import application.models.dtos.responses.TokenPairResponse;
import application.services.AuthService;
import core.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Các API liên quan đến xác thực và phân quyền")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Đăng nhập hệ thống", description = "Trả về Access Token và Refresh Token sau khi xác thực thành công")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenPairResponse> login(
            @Valid @RequestBody LoginRequest rq,
            @Parameter(description = "ID duy nhất của thiết bị (UUID/IMEI...)", required = true)
            @RequestHeader("x-device-id") String deviceId
    ) {
        return ApiResponse.success(authService.login(rq, deviceId));
    }

    @Operation(summary = "Làm mới Access Token", description = "Sử dụng Refresh Token để lấy Access Token mới khi cái cũ hết hạn")
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenPairResponse> refresh(
            @Valid @RequestBody RefreshRequest rq,
            @RequestHeader("x-device-id") String deviceId
    ) {
        return ApiResponse.success(authService.refreshSession(rq, deviceId));
    }

    @Operation(summary = "Đăng xuất", description = "Xóa session của thiết bị hiện tại trong hệ thống")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> logout(
            @Valid @RequestBody RefreshRequest rq,
            @RequestHeader("x-device-id") String deviceId
    ) {
        authService.logout(rq, deviceId);
        return ApiResponse.success(null);
    }
}
