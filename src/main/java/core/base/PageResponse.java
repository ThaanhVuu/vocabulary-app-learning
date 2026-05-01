package core.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Builder
public class PageResponse<T> {
    private List<T> items;
    private int     page;
    private int     size;
    private long    totalElements;
    private int     totalPages;
}
