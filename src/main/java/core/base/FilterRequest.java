package core.base;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FilterRequest {
    private String field;       // VD: "email"
    private Operator operator;  // VD: Operator.LIKE
    private Object value;       // VD: "gmail.com"

    public enum Operator {
        EQ,      // Bằng (=)
        NEQ,     // Khác (!=)
        GT,      // Lớn hơn (>)
        LT,      // Nhỏ hơn (<)
        GTE,     // Lớn hơn hoặc bằng (>=)
        LTE,     // Nhỏ hơn hoặc bằng (<=)
        LIKE,    // Tìm kiếm chuỗi (LIKE %...%)
        IN,      // Nằm trong danh sách (IN)
        BW,      // Between
    }
}