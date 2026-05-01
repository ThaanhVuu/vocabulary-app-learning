package core.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String code;
    private String message;
    private T      data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0", "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("0", message, data);
    }

    public static <T> ApiResponse<T> error(IErrorCode errorCode){
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(IErrorCode errorCode, T data){
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), data);
    }
}
