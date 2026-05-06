package core.base;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

public class BaseResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // ✅ Bỏ qua SpringDoc
        String declaringClass = returnType.getDeclaringClass().getName();
        if (declaringClass.startsWith("org.springdoc") || declaringClass.startsWith("org.springframework")) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 1. Nếu API trả về chuỗi (String), Spring có Converter riêng, nên ta bỏ qua để tránh lỗi ClassCastException.
        // Thực tế hiếm khi API REST trả về chuỗi String trơn.
        if (body instanceof String) {
            return body;
        }

        // 2. Nếu Controller đã cố tình trả về ApiResponse rồi (ví dụ trong Exception Handler),
        // thì ta không bọc thêm lớp nữa (tránh ApiResponse lồng ApiResponse).
        if (body instanceof ApiResponse) {
            return body;
        }

        // 3. Nếu là các trường hợp khác (như TopicRequest, PageResponse, UserRequest...),
        // ta tự động bọc nó vào ApiResponse.success()
        return ApiResponse.success(body);
    }
}
