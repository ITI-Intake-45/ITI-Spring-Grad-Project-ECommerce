package gov.iti.jet.ewd.ecom.mapper;

import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDTO(User user);
    User toEntity(UserDto dto);
}