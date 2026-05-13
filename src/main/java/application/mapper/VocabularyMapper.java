package application.mapper;

import application.models.dtos.requests.VocabularyDTO;
import application.models.entities.Vocabulary;
import core.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VocabularyMapper extends BaseMapper<Vocabulary, VocabularyDTO> {

    // Cập nhật dữ liệu từ DTO vào Entity có sẵn (Tránh tạo mới Object)
    void updateEntityFromDto(VocabularyDTO dto, @MappingTarget Vocabulary entity);
}