package core.base;

import core.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.function.Supplier;

@Slf4j
@Transactional(rollbackFor = Exception.class)
public abstract class BaseService<T extends BaseEntity, DTO extends BaseDTO, ID extends Serializable, R extends JpaRepository<T, ID>> {

    public final R repository;
    protected BaseMapper<T, DTO> mapper;

    public BaseService(R repository, BaseMapper<T, DTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ==========================================
    // 1. LÕI THỰC THI (PHỄU LỌC LỖI)
    // ==========================================
    protected <Result> Result execute(Supplier<Result> action) throws AppException {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            throw handleDbException(ex); // Dịch lỗi SQL
        } catch (AppException ex) {
            log.error("App Exception: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("System Exception: ", ex);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private AppException handleDbException(DataIntegrityViolationException ex) {
        String error = ex.getMostSpecificCause().getMessage();
        log.error("Database constraint violated: {}", error);

        if (error == null) {
            return new AppException(ErrorCode.INTERNAL_SERVER_ERROR); // Hoặc mã lỗi chung chung của bạn
        }

        // Nhận diện lỗi trùng lặp dữ liệu (Unique Constraint) cho cả MySQL và Postgres
        if (error.contains("Duplicate entry") || error.contains("duplicate key value")) {
            // Chỉ trả về ErrorCode chuẩn hóa, không cần bóc tách chuỗi nữa
            return new AppException(ErrorCode.DUPLICATE_DATA);
        }

        // Các lỗi khóa ngoại, null check... dưới database
        return new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // ==========================================
    // 2. CRUD OPERATIONS
    // ==========================================

    public DTO create(DTO dto) throws AppException {
        return execute(() -> {
            T entity = mapper.toEntity(dto);
            beforeCreate(dto, entity);

            entity = repository.save(entity);

            afterCreate(dto, entity);
            return mapper.toDto(entity);
        });
    }

    public DTO update(DTO dto) throws AppException {
        return execute(() -> {
            T oldData = get((ID) dto.getId()); // Đảm bảo dữ liệu tồn tại
            T entity = mapper.toEntity(dto);
            beforeUpdate(dto, entity);

            // Spring Boot tự động cập nhật modifiedAt và lastModifiedBy
            entity = repository.save(entity);

            afterUpdate(dto, entity);
            return mapper.toDto(entity);
        });
    }

    /**
     * XÓA MỀM (SOFT DELETE)
     * Thay vì xóa hẳn (repository.delete), ta đổi trạng thái isDeleted = true
     */
    public void delete(ID id) throws AppException {
        execute(() -> {
            T entity = get(id);
            entity.setDeleted(true); // Nhờ biến boolean isDeleted trong BaseEntity
            repository.save(entity);
            return null;
        });
    }

    public T get(ID id) throws AppException {
        return repository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    // ==========================================
    // 3. HOOKS (Dành cho class con tự thêm logic)
    // ==========================================

    // Đã xóa sạch việc gán ngày giờ vì AuditingEntityListener đã lo.
    // Các hàm này giờ chỉ để class con (như UserService) gắn thêm logic (VD: Mã hóa mật khẩu)
    protected void beforeCreate(DTO dto, T entity) {}
    protected void beforeUpdate(DTO dto, T entity) {}
    protected void afterCreate(DTO dto, T entity) {}
    protected void afterUpdate(DTO dto, T entity) {}
}