package core.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "Request object for searching, pagination and dynamic filtering")
public class SearchRequest {

    @Schema(description = "Page number (starting from 0)", example = "0")
    private int page = 0;

    @Schema(description = "Number of records per page", example = "10")
    private int size = 10;

    @Schema(description = "Field to sort by", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Sort direction", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDir = "DESC";

    @Schema(description = "List of filter conditions (WHERE clause)")
    private List<FilterRequest> filters = new ArrayList<>();
}