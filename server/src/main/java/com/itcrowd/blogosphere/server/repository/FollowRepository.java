package com.itcrowd.blogosphere.server.repository;

import com.itcrowd.blogosphere.server.model.Follow;
import com.itcrowd.blogosphere.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, Long> {


    Optional<Follow>findByUserAndTarget (User user, User target);


}