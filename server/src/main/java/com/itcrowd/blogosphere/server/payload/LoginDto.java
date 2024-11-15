package com.itcrowd.blogosphere.server.payload;

import lombok.Data;

@Data
public class LoginDto {

    private String username;

    private String password;
}