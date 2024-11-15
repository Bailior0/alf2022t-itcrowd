package com.itcrowd.blogosphere.server.payload;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PostDto {

    UUID id;

    String title;

    String content;

    UUID author;

    LocalDateTime createDate;

    int voteCount;
}
