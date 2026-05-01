# Chỉ dùng JRE (môi trường chạy) siêu nhẹ, bản Java 25 khớp với code của bạn
FROM eclipse-temurin:25-jre-alpine

# Cấu hình thư mục làm việc bên trong Container
WORKDIR /app

# Tạo user không có quyền root để bảo mật (Optional nhưng rất khuyên dùng)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy thẳng file .jar ĐÃ BUILD SẴN từ thư mục build/libs/ của Gradle vào container
# Đổi tên nó thành app.jar cho gọn
COPY build/libs/*.jar app.jar

# Khai báo cổng
EXPOSE 8080

# Khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]