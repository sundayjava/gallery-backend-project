package com.gallery.gallery.services;

import com.gallery.gallery.dao.LoginRequest;
import com.gallery.gallery.dao.SignupRequest;
import com.gallery.gallery.exceptions.AlreadyExistException;
import com.gallery.gallery.models.Role;
import com.gallery.gallery.models.User;
import com.gallery.gallery.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager manager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager manager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new AlreadyExistException(signupRequest.getEmail());
        }

        String strRoles = signupRequest.getRole();
        Role roles;

        if ("admin".equals(strRoles)) {
            roles = Role.ADMIN;
        } else if (strRoles == null) {
            roles = Role.USER;
        } else {
            roles = Role.USER;
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFullName(signupRequest.getFullName());
        user.setRole(roles);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        return Optional.of(userRepository.save(user));
    }

    public Optional<Authentication> login(LoginRequest loginRequest) {
        return Optional.ofNullable(manager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        ));
    }

    public Optional<User> getProfile(UUID id) {
        User me = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this id" + id));

        return Optional.ofNullable(me);
    }
}
