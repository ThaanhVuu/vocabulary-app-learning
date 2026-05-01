package core.base;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final IErrorCode errorCode;

    public AppException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(IErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
