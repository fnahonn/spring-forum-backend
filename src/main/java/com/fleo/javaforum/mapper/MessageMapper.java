package com.fleo.javaforum.mapper;

import com.fleo.javaforum.model.Message;
import com.fleo.javaforum.payload.response.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface MessageMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "author", source = "author")
    @Mapping(target = "accepted", source = "accepted")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    MessageResponse toResponse(Message message);
}
