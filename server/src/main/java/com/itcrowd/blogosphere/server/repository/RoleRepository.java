package com.itcrowd.blogosphere.server.repository;

import com.itcrowd.blogosphere.server.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}