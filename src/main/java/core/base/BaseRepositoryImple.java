package core.base;

import core.utils.DynamicSpecification;
import core.utils.PageableUtils;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

public class BaseRepositoryImple<T extends BaseEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    public BaseRepositoryImple(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> search(SearchRequest request) {
        Class<T> entityClass = getDomainClass();
        Specification<T> spec = DynamicSpecification.build(request.getFilters(), entityClass);
        Pageable pageable = PageableUtils.build(request, entityClass);
        return findAll(spec, pageable);
    }
}