package io.github.kacper.weglarz.realtimecollaboration.controller;

import io.github.kacper.weglarz.realtimecollaboration.entity.User;
import io.github.kacper.weglarz.realtimecollaboration.exceptions.UserNotFoundException;
import io.github.kacper.weglarz.realtimecollaboration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets user profile
     * @param auth Authentication data of the user
     * @return user profile data
     */
    @GetMapping("/profile")
    public User getProfile(Authentication auth) {
        String username = auth.getName();
        return userService.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("User not found " + username));

    }
}
