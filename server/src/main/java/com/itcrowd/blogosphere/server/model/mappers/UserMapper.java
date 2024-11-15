package com.itcrowd.blogosphere.server.model.mappers;

import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.payload.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

@Mapper
public abstract class UserMapper implements Converter<User, UserDto> {

    @Mapping(source="roles", target="roles", qualifiedByName="customRoleMapper")
    public abstract UserDto convert(User car);

    @Named(value = "customRoleMapper")
    public static List<String> customRoleMapper(List<Role> roles){
        return roles.stream().map(Role::getName).toList();
    }
}