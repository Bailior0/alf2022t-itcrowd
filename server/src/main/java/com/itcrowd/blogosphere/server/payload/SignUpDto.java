package com.itcrowd.blogosphere.server.payload;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class SignUpDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}