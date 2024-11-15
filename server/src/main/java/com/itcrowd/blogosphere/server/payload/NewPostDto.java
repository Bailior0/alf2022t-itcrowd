package com.itcrowd.blogosphere.server.payload;

import com.itcrowd.blogosphere.server.model.User;
import lombok.Data;

@Data
public class NewPostDto {

    String title;

    String content;

    User author;
}
