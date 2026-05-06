package core.base;

import core.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<?> handleAppException(AppException ex) {
        log.error("AppException caught: ", ex);
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // 1. Tạo một Map để chứa tên trường bị lỗi và câu thông báo
        Map<String, String> errors = new HashMap<>();

        // 2. Lặp qua tất cả các lỗi Validation mà Spring bắt được
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Lấy tên biến (vd: password)
            String errorMessage = error.getDefaultMessage();    // Lấy câu thông báo (vd: Mật khẩu phải từ...)
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation error: {}", errors);

        // 3. Trả về cho người dùng.
        // LƯU Ý: ApiResponse của bạn phải hỗ trợ việc truyền thêm dữ liệu (errors) vào.
        // Mình giả sử ApiResponse có hàm error(ErrorCode, Object data)
        return ResponseEntity
                .status(ErrorCode.INVALID_PROPERTIES.getHttpStatus())
                // Bạn cần sửa lại hàm error trong ApiResponse để nhận Map errors này nhé
                .body(ApiResponse.error(ErrorCode.INVALID_PROPERTIES, errors));
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingRequestHeaderException(MissingRequestHeaderException ex){
        return buildErrorResponse(ErrorCode.INVALID_HEADER, ex);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildErrorResponse(ErrorCode.INVALID_PAYLOAD, ex);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handlingException(Exception ex) {
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex);
    }

    // Helper method dùng chung cho các lớp con
    protected ResponseEntity<ApiResponse<?>> buildErrorResponse(ErrorCode errorCode, Exception e) {
        log.error("Exception caught: ", e);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }
}
