package application.repositories;

import application.models.entities.QuizResult;
import core.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizResultRepository extends BaseRepository<QuizResult, Long> {

    /**
     * Lấy lịch sử thi của một user, sắp xếp theo thời gian mới nhất.
     * Không load answers để tối ưu performance ở list view.
     */
    Page<QuizResult> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Lấy chi tiết 1 bài thi kèm toàn bộ câu trả lời.
     * JOIN FETCH để tránh N+1 query.
     * Điều kiện userId đảm bảo user chỉ xem bài của chính mình.
     */
    @Query("""
            SELECT r FROM QuizResult r
            LEFT JOIN FETCH r.answers a
            LEFT JOIN FETCH a.vocabulary
            WHERE r.id = :id
              AND r.user.id = :userId
              AND r.isDeleted = false
            """)
    Optional<QuizResult> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}