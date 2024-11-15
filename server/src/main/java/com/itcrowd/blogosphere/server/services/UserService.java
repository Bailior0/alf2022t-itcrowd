package com.itcrowd.blogosphere.server.services;

import com.itcrowd.blogosphere.server.model.User;
import com.itcrowd.blogosphere.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository repository;

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            String currentPrincipalName = authentication.getName();

            var user = repository.findByUsername(currentPrincipalName);
            if(user.isPresent()){
                return user.get();
            }
        }
        return null;
    }
}
