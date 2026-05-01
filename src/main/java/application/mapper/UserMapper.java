package application.mapper;

import application.models.dtos.requests.UserRequest;
import application.models.entities.User;
import core.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<User, UserRequest> {
}