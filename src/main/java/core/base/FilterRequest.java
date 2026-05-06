package core.base;

import core.constants.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Chi tiết bộ lọc điều kiện")
public class FilterRequest {

    @Schema(description = "Tên trường cần lọc (phải khớp với field trong Entity)", example = "name")
    private String field;

    @Schema(description = "Toán tử so sánh", example = "LIKE",
            allowableValues = {"EQ", "NEQ", "LIKE", "GT", "LT", "GTE", "LTE", "IN"})
    private Operator operator;

    @Schema(description = "Giá trị cần lọc", example = "Java Programming")
    private Object value;
}