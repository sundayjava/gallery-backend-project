package com.gallery.gallery.controllers;

import com.gallery.gallery.dao.ApiResponse;
import com.gallery.gallery.dao.LoginRequest;
import com.gallery.gallery.dao.SignupRequest;
import com.gallery.gallery.exceptions.BadRequestExceptions;
import com.gallery.gallery.models.User;
import com.gallery.gallery.services.UserService;
import com.gallery.gallery.services.authServices.CustomUserDetails;
import com.gallery.gallery.services.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:5173/")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        return userService.signup(signupRequest)
                .map(newUser -> ResponseEntity.ok(new ApiResponse(newUser, true, "")))
                .orElseThrow(() -> new BadRequestExceptions("User registration failed"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response, HttpServletRequest request) {
        Authentication authentication = userService.login(loginRequest)
                .orElseThrow(() -> new BadRequestExceptions("Couldn't login user"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(customUserDetails.getUsername(), customUserDetails.getId());
        Cookie cookie = jwtService.generateCookieFromJwt(token);
        response.addCookie(cookie);

        Optional<User> data = userService.getProfile(customUserDetails.getId());
        return ResponseEntity.ok(new ApiResponse(data, true, token));
    }

    @GetMapping("/active/user")
    public ResponseEntity<User> retrieveActiveUser(HttpServletRequest request){
        UUID id = UUID.fromString(jwtService.extractId(request));
        User data = userService.getProfile(id)
                .orElseThrow(() -> new BadRequestExceptions("No user found"));
        return ResponseEntity.ok(data);
    }
}
