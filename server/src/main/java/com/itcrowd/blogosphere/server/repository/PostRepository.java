package com.itcrowd.blogosphere.server.repository;

import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends CrudRepository<Post, UUID> {
    List<Post> findTop10ByOrderByCreateDate();
    Page<Post> findAllByOrderByCreateDateDesc(Pageable pageable);
    Page<Post> findAllByParentIsNullOrderByCreateDateDesc(Pageable pageable);
    Page<Post> getPostsByParentOrderByCreateDateDesc (Post parent,Pageable pageable);



    @Query("SELECT p from Post p where exists (select fo.target from Follow fo where fo.user = ?1 and fo.target = p.author) and p.parent = null")
    Page<Post> getUserFeed(User user, Pageable pageable);
}