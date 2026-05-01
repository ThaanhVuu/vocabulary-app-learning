package core.utils;

import core.base.AppException;
import core.base.FilterRequest;
import core.constants.ErrorCode;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

import static core.utils.SearchableHelper.isSearchable;

public class DynamicSpecification {
    public static <T> Specification<T> build(List<FilterRequest> filters, Class<T> entityClass) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();
            for (FilterRequest filter : filters) {
                String field = filter.getField();

                if (!isSearchable(entityClass, field)) {
                    throw new AppException(ErrorCode.INVALID_PARAM, "Filtering by " + filter + " is not allowed: ");
                }

                Object value = filter.getValue();
                switch (filter.getOperator()) {
                    case EQ     -> predicates.add(cb.equal(root.get(field), value));
                    case NEQ    -> predicates.add(cb.notEqual(root.get(field), value));
                    case LIKE   -> predicates.add(cb.like(cb.lower(root.get(field).as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                    case GT     -> predicates.add(cb.greaterThan(root.get(field), (Comparable) value));
                    case LT     -> predicates.add(cb.lessThan(root.get(field), (Comparable) value));
                    case GTE    -> predicates.add(cb.greaterThanOrEqualTo(root.get(field), (Comparable) value));
                    case LTE    -> predicates.add(cb.lessThanOrEqualTo(root.get(field), (Comparable) value));
                    case IN     ->
                    {
                        if (value instanceof List<?> list) predicates.add(root.get(field).in(list));
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}