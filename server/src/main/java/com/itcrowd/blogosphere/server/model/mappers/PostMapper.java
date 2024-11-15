package com.itcrowd.blogosphere.server.model.mappers;

import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.payload.PostDto;
import com.itcrowd.blogosphere.server.repository.PostVoteRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

@Mapper(
        componentModel = "spring"
)
public abstract class PostMapper implements Converter<Post, PostDto> {

    @Mappings({
      @Mapping(target="author", source="author.id"),
      @Mapping(target = "voteCount", ignore = true)
    })
    public abstract PostDto convert(Post post);

    @Autowired
    private PostVoteRepository voteRepository;

    @AfterMapping // or @BeforeMapping
    void calculateTotal(Post p, @MappingTarget PostDto dto) {
        dto.setVoteCount(voteRepository.getPostVoteSum(p).orElse(0));
    }
}
