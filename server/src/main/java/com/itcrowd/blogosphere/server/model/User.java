package com.itcrowd.blogosphere.server.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.MODULE)
public class User {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    String username;

    String email;

    String password;

    @OneToMany
    List<Role> roles;

    private LocalDateTime registerDate;

    public User(){ }
}


