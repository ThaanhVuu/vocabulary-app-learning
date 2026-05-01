package core.utils;

import java.lang.reflect.Field;

public class SearchableHelper {
    public static boolean isSearchable(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                return field.isAnnotationPresent(Searchable.class);
            } catch (NoSuchFieldException e) {
                // Nếu không thấy ở class con (User), nhảy lên class cha (BaseEntity) tìm tiếp
                currentClass = currentClass.getSuperclass();
            }
        }
        return false;
    }
}
