package core.constants;

import core.base.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements IErrorCode {
    INTERNAL_SERVER_ERROR("0001", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("0002", "Not found", HttpStatus.NOT_FOUND),
    INVALID_PAYLOAD("0003", "Missing request body", HttpStatus.BAD_REQUEST),
    INVALID_HEADER("0004", "Missing request header", HttpStatus.BAD_REQUEST),
    INVALID_PARAM("0005", "Param is not valid", HttpStatus.BAD_REQUEST),
    DUPLICATE_DATA("0006", "Data existed and unique", HttpStatus.CONFLICT),
    INVALID_TOKEN("0007", "Invalid session!", HttpStatus.UNAUTHORIZED),
    EXPIRED_SESSION("0008", "Session expired", HttpStatus.UNAUTHORIZED),
    INVALID_PROPERTIES("0009", "Invalid properties", HttpStatus.UNPROCESSABLE_CONTENT),
            ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
