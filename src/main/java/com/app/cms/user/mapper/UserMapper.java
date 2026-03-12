package com.app.cms.user.mapper;

import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.User;
import org.mapstruct.Mapper;

import org.mapstruct.Builder;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {
    User toEntity(UserDto dto);

    UserDto toDto(User user);
}
