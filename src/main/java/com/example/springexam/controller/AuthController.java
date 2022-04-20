package com.example.springexam.controller;

import com.example.springexam.DTO.LoginDTO;
import com.example.springexam.security.AuthService;
import com.example.springexam.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;


    private final AuthService authService;


    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDTO loginDTO) {
        UserDetails userDetails = authService.loadUserByUsername(loginDTO.getUserName());

        if (userDetails != null) {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            String token = jwtProvider.generateToken(loginDTO.getUserName());
            return ResponseEntity.ok().body(token);
        } else {
            return ResponseEntity.status(401).body("UnAuthorized");
        }
    }
}
