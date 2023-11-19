package com.fleo.javaforum.mapper;

import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.response.TopicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TopicMapper {

    @Mapping(target="id", source="id")
    @Mapping(target="name", source="name")
    @Mapping(target="content", source="content")
    @Mapping(target="author", source="author")
    @Mapping(target="solved", source="solved")
    @Mapping(target="createdAt", source="createdAt")
    @Mapping(target="updatedAt", source="updatedAt")
    TopicResponse toResponse(Topic topic);
}
