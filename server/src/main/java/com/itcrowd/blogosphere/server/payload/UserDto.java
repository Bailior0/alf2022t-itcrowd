package com.itcrowd.blogosphere.server.payload;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserDto {

    UUID id;

    String username;

    String email;

    List<String> roles;
}
