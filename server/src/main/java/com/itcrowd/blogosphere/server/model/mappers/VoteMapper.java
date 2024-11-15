package com.itcrowd.blogosphere.server.model.mappers;

import com.itcrowd.blogosphere.server.model.PostVote;
import com.itcrowd.blogosphere.server.payload.VoteDTO;
import org.mapstruct.*;
import org.springframework.core.convert.converter.Converter;

@Mapper(
        componentModel = "spring"
)
public abstract class VoteMapper implements Converter<PostVote, VoteDTO> {

    @Mappings({
        @Mapping(target = "post", source = "post.id"),
        @Mapping(target = "user", source = "user.id")
    })
    public abstract VoteDTO convert(PostVote post);
}
