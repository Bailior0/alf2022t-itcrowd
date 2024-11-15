package com.itcrowd.blogosphere.server.repository;

import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    @Query("select sum(v.amount) from PostVote v where v.post = ?1")
    Optional<Integer> getPostVoteSum(Post p);
}