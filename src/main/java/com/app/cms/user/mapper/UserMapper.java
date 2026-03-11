package com.app.cms.user.mapper;

import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);
    UserDto toDto(User user);
}
