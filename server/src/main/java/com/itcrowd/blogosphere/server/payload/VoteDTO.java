package com.itcrowd.blogosphere.server.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class VoteDTO {

    UUID post;

    UUID user;

    int amount;
}
