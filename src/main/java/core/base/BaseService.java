package core.base;

import core.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Lớp dịch vụ cơ sở (Base Service) cung cấp các thao tác CRUD cơ bản,
 * quản lý giao dịch, và xử lý ngoại lệ tập trung cho toàn bộ hệ thống.
 *
 * @param <T>   Kiểu thực thể (Entity)
 * @param <DTO> Kiểu đối tượng truyền tải dữ liệu (Data Transfer Object)
 * @param <ID>  Kiểu dữ liệu của khóa chính
 * @param <R>   Kiểu Repository (Kế thừa từ BaseRepository)
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
public abstract class BaseService<T extends BaseEntity, DTO extends BaseDTO, ID extends Serializable, R extends BaseRepository<T, ID>> {

    protected final R repository;
    protected BaseMapper<T, DTO> mapper;

    public BaseService(R repository, BaseMapper<T, DTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ==========================================
     * 1. LÕI THỰC THI (PHỄU LỌC LỖI)
     * ==========================================
     * Hàm bọc logic để bắt và chuẩn hóa các ngoại lệ sinh ra trong quá trình thực thi DB.
     *
     * @param action   Khối lệnh thực thi
     * @param <Result> Kiểu dữ liệu trả về
     * @return Kết quả của action
     * @throws AppException Lỗi đã được chuẩn hóa theo hệ thống ErrorCode
     */
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

    /**
     * Xử lý các lỗi ràng buộc Database (Unique, Not Null, Foreign Key...).
     *
     * @param ex Ngoại lệ từ DB
     * @return AppException đã được phân loại
     */
    private AppException handleDbException(DataIntegrityViolationException ex) {
        String error = ex.getMostSpecificCause().getMessage();
        log.error("Database constraint violated: {}", error);

        if (error == null) {
            return new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // Nhận diện lỗi trùng lặp dữ liệu (Unique Constraint) cho cả MySQL và Postgres
        if (error.contains("Duplicate entry") || error.contains("duplicate key value")) {
            return new AppException(ErrorCode.DUPLICATE_DATA);
        }

        return new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /*
      ==========================================
      2. CRUD OPERATIONS
      ==========================================
     */

    /**
     * Tạo mới dữ liệu.
     *
     * @param dto Dữ liệu gửi lên từ Client
     * @return DTO sau khi được lưu thành công
     */
    public DTO create(DTO dto) throws AppException {
        return execute(() -> {
            T entity = mapper.toEntity(dto);

            beforeCreate(dto, entity);
            entity = repository.save(entity);
            afterCreate(dto, entity);

            return mapper.toDto(entity);
        });
    }

    /**
     * Cập nhật dữ liệu có sẵn.
     *
     * @param dto Dữ liệu cần cập nhật (yêu cầu phải có ID)
     * @return DTO sau khi được cập nhật thành công
     */
    public DTO update(DTO dto) throws AppException {
        return execute(() -> {
            T oldData = get((ID) dto.getId()); // Đảm bảo dữ liệu tồn tại
            T entity = mapper.toEntity(dto);

            beforeUpdate(dto, entity, oldData);

            // Spring Boot tự động cập nhật modifiedAt và lastModifiedBy thông qua JPA Auditing
            entity = repository.save(entity);

            afterUpdate(dto, entity);

            return mapper.toDto(entity);
        });
    }

    /**
     * Xóa mềm (Soft Delete). Đổi trạng thái isDeleted = true thay vì xóa khỏi DB.
     *
     * @param id Khóa chính của bản ghi
     */
    public void delete(ID id) throws AppException {
        execute(() -> {
            beforeDelete(id);
            T entity = get(id);

            entity.setDeleted(true);
            repository.save(entity);

            afterDelete(id, entity);
            return null;
        });
    }

    /**
     * Lấy dữ liệu chi tiết theo ID.
     *
     * @param id Khóa chính
     * @return Entity tương ứng
     */
    public T get(ID id) throws AppException {
        beforeGet(id);
        T entity = repository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        afterGet(id, entity);

        return entity;
    }

    /**
     * Lấy dữ liệu chi tiết và tự động chuyển đổi sang DTO.
     *
     * @param id Khóa chính
     * @return DTO tương ứng
     */
    public DTO getById(ID id) throws AppException {
        return mapper.toDto(get(id));
    }

    /**
     * Tìm kiếm, lọc động và phân trang.
     *
     * @param request Cấu trúc yêu cầu tìm kiếm (page, size, filters...)
     * @return Trang (Page) chứa danh sách kết quả đã map sang DTO
     */
    public PageResponse<DTO> search(SearchRequest request) throws AppException {
        return execute(() -> {
            beforeSearch(request);

            Page<T> entityPage = repository.search(request);
            Page<DTO> dtoPage = entityPage.map(mapper::toDto);

            afterSearch(request, dtoPage);
            return PageResponse.<DTO>builder()
                    .items(dtoPage.getContent())
                    .page(dtoPage.getNumber())
                    .size(dtoPage.getSize())
                    .totalElements(dtoPage.getTotalElements())
                    .totalPages(dtoPage.getTotalPages())
                    .build();
        });
    }

    /**
     * ==========================================
     * 3. HOOKS (Dành cho class con tự thêm logic)
     * ==========================================
     * Các hàm này bị bỏ trống (default) ở Base.
     * Kế thừa Service con và @Override để "cắm" thêm logic nghiệp vụ tùy chỉnh.
     */

    /** Can thiệp trước khi lưu mới (VD: Gắn thêm userId từ SecurityContext, mã hóa mật khẩu) */
    protected void beforeCreate(DTO dto, T entity) {}

    /** Can thiệp sau khi lưu mới thành công (VD: Gửi email thông báo, log lịch sử) */
    protected void afterCreate(DTO dto, T entity) {}

    /**
     * Can thiệp trước khi cập nhật.
     * @param oldData Thực thể cũ đang lưu trong Database để tiện so sánh sự thay đổi
     */
    protected void beforeUpdate(DTO dto, T entity, T oldData) {}

    /** Can thiệp sau khi cập nhật thành công */
    protected void afterUpdate(DTO dto, T entity) {}

    /** Can thiệp trước khi thực hiện xóa (VD: Kiểm tra quyền xóa) */
    protected void beforeDelete(ID id) {}

    /** Can thiệp sau khi xóa thành công (VD: Xóa các file ảnh đính kèm liên quan) */
    protected void afterDelete(ID id, T entity) {}

    /** Can thiệp trước khi lấy chi tiết 1 bản ghi */
    protected void beforeGet(ID id) {}

    /** Can thiệp sau khi lấy ra thành công */
    protected void afterGet(ID id, T entity) {}

    /** Can thiệp trước khi tìm kiếm (VD: Ép buộc thêm Filter tìm theo người đang đăng nhập) */
    protected void beforeSearch(SearchRequest request) {}

    /** Can thiệp sau khi có kết quả tìm kiếm (VD: Chỉnh sửa lại list DTO trước khi trả về) */
    protected void afterSearch(SearchRequest request, Page<DTO> result) {}

    /** Can thiệp trước khi lấy dữ liệu dạng DTO */
    protected void beforeGetById(ID id) {}

    /** Can thiệp sau khi lấy dữ liệu dạng DTO thành công */
    protected void afterGetById(ID id, DTO dto) {}
}