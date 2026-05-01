package core.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.utils.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long            id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Searchable
    private LocalDateTime   createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Searchable
    private LocalDateTime   modifiedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long            createdBy = 0L;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private Long            lastModifiedBy = 0L;

    @SQLRestriction("is_deleted = false")
    @Column(name = "is_deleted")
    private boolean         isDeleted = false;
}