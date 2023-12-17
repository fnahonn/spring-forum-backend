package com.fleo.javaforum.mapper;

import com.fleo.javaforum.model.Topic;
import com.fleo.javaforum.payload.response.TopicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MessageMapper.class})
public interface TopicMapper {

    @Mapping(target="id", source="id")
    @Mapping(target="name", source="name")
    @Mapping(target="content", source="content")
    @Mapping(target="author", source="author")
    @Mapping(target="solved", source="solved")
    @Mapping(target = "messages", source = "messages")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target="createdAt", source="createdAt")
    @Mapping(target="updatedAt", source="updatedAt")
    TopicResponse toResponse(Topic topic);

    @Mapping(target="id", source="id")
    @Mapping(target="name", source="name")
    @Mapping(target="content", source="content")
    @Mapping(target="author", source="author")
    @Mapping(target="solved", source="solved")
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "lastMessage", source = "lastMessage")
    @Mapping(target="createdAt", source="createdAt")
    @Mapping(target="updatedAt", source="updatedAt")
    TopicResponse toResponseWithoutMessages(Topic topic);


    default Iterable<TopicResponse> map(Page<Topic> topicIterable) {
        if ( topicIterable == null ) {
            return null;
        }

        ArrayList<TopicResponse> iterable = new ArrayList<TopicResponse>();
        for (Topic topic : topicIterable) {
            iterable.add(toResponseWithoutMessages(topic));
        }

        return new PageImpl<>(iterable, topicIterable.getPageable(), topicIterable.getSize());
    }


}

