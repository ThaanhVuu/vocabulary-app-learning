package core.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "DESC";

    private List<FilterRequest> filters;
}
