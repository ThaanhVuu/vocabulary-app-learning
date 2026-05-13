package core.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);

    List<D> toDtoList(Collection<E> entityList);
    List<E> toEntityList(Collection<D> dtoList);

    Set<D> toDtoSet(Collection<E> entityList);
    Set<E> toEntitySet(Collection<D> dtoList);

    default Set<D> mapEntityMapToDtoSet(Map<String, E> entityMap) {
        if (entityMap == null) return null;
        return toDtoSet(entityMap.values());
    }
}