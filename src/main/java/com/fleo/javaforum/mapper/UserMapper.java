package com.fleo.javaforum.mapper;

import com.fleo.javaforum.payload.response.UserResponse;
import com.fleo.javaforum.security.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "pseudo", source = "pseudo")
    UserResponse toResponse(User user);
}
