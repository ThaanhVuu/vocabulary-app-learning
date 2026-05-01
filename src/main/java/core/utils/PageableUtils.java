package core.utils;

import core.base.AppException;
import core.base.SearchRequest;
import core.constants.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import static core.utils.SearchableHelper.isSearchable;

public class PageableUtils {

    public static <T> Pageable build(SearchRequest request, Class<T> entityClass) {

        String sortBy = request.getSortBy();
        if (!StringUtils.hasText(sortBy)) {
            sortBy = "id";
        }

        if (!isSearchable(entityClass, sortBy)) {
            throw new AppException(ErrorCode.INVALID_PARAM, "Sorting by '" + sortBy + "' is not allowed.");
        }

        String sortDir = request.getSortDir();
        Sort.Direction direction = (StringUtils.hasText(sortDir) && sortDir.equalsIgnoreCase("ASC"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC; // Mặc định là DESC (mới nhất lên đầu)

        int page = request.getPage();

        return PageRequest.of(page, request.getSize(), Sort.by(direction, sortBy));
    }
}