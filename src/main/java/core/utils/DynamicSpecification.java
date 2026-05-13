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

    private static <T> jakarta.persistence.criteria.Path<?> getPath(
            jakarta.persistence.criteria.Root<T> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.", 2);
            return root.get(parts[0]).get(parts[1]);
        }
        return root.get(field);
    }

    public static <T> Specification<T> build(List<FilterRequest> filters, Class<T> entityClass) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            for (FilterRequest filter : filters) {
                String field = filter.getProperty();

                // ✅ bỏ qua check @Searchable cho nested field
                if (!field.contains(".") && !isSearchable(entityClass, field)) {
                    throw new AppException(ErrorCode.INVALID_PARAM,
                            "Filtering by " + filter + " is not allowed: ");
                }

                Object value = filter.getValue();
                var path = getPath(root, field);

                switch (filter.getOperator()) {
                    case EQ   -> predicates.add(cb.equal(path, value));
                    case NEQ  -> predicates.add(cb.notEqual(path, value));
                    case LIKE -> predicates.add(cb.like(
                            cb.lower(path.as(String.class)),
                            "%" + value.toString().toLowerCase() + "%"));
                    case GT   -> predicates.add(cb.greaterThan(path.as(Comparable.class), (Comparable) value));
                    case LT   -> predicates.add(cb.lessThan(path.as(Comparable.class), (Comparable) value));
                    case GTE  -> predicates.add(cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                    case LTE  -> predicates.add(cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                    case IN   -> {
                        if (value instanceof List<?> list) predicates.add(path.in(list));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}