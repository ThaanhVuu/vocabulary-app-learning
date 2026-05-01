package core.base;

import org.springframework.http.HttpStatus;

public interface IErrorCode {
    String      getCode();
    String      getMessage();
    HttpStatus  getHttpStatus();
}
