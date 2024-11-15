package com.itcrowd.blogosphere.server.repository.preloaddata;

import com.itcrowd.blogosphere.server.model.Follow;
import com.itcrowd.blogosphere.server.model.Post;
import com.itcrowd.blogosphere.server.model.Role;
import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.FollowRepository;
import com.itcrowd.blogosphere.server.repository.PostRepository;
import com.itcrowd.blogosphere.server.repository.RoleRepository;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
class LoadDatabase {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FollowRepository followRepository;

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository, PostRepository postRepository, RoleRepository roleRepository) {
        return args -> {
            log.info("Preloading ");

            var admin_role =new Role("ADMIN");
            roleRepository.save(admin_role);

            repository.save(
                    User.builder()
                            //.id(UUID.fromString("197cd936-a5cf-44b1-915e-c42a99cf1c2d"))
                            .password(passwordEncoder.encode("admin"))
                            .username("admin")
                            .roles(List.of(admin_role))
                            .build()
            );

            var user = repository.save(
                    User.builder()
                            .password(passwordEncoder.encode("asdasd"))
                            .username("Test user 1")
                            .build()
            );

            if(user != null)
                GenerateUserPosts(postRepository, user);

            var user2 = repository.save(
                    User.builder()
                            .password(passwordEncoder.encode("asdasd"))
                            .username("Test user 2")
                            .build()
            );

            followRepository.save(
                    Follow.builder()
                            .user(user)
                            .target(user2)
                            .build()
            );

            if(user2 != null)
                GenerateUserPosts(postRepository, user2);
        };
    }

    private void GenerateUserPosts(PostRepository postRepository, User user) {
        for (int i = 0; i < 30; i++) {
            var num = 30-i;

            var post = Post.builder()
                    .title("Post #" + num + "from " + user.getUsername())
                    .createDate(LocalDateTime.now().minusDays(i))
                    .content("This is the content for the post number " + num + "made on " + LocalDate.now().minusDays(i))
                    .author(user)
                    .build();
            post = postRepository.save(post);

            for (int j = 0; j < 5; j++) {
                var sub = Post.builder()
                        .title("Post #" + num + "Sub post #" + j)
                        .createDate(LocalDateTime.now().minusDays(i).plusMinutes(5*j))
                        .content("This is the content for the sub post")
                        .author(user)
                        .parent(post)
                        .build();
                postRepository.save(sub);
            }
        }
    }
}