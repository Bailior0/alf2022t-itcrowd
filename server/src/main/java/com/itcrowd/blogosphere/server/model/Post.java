package com.itcrowd.blogosphere.server.model;

import com.itcrowd.blogosphere.server.payload.NewPostDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "post")
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    private String title;

    @Nationalized
    private String content;

    private LocalDateTime createDate;

    private boolean archive;

    @OneToMany(cascade = {CascadeType.REMOVE})
    private List<Post> comments;

    @ManyToOne()
    @JoinColumn(name="parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post parent;


    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;


    public Post(){ }

    public static Post fromNew(NewPostDto newPost) {
        var res = new Post();
        res.title = newPost.getTitle();
        res.content = newPost.getContent();

        res.author = newPost.getAuthor();

        return res;
    }
}