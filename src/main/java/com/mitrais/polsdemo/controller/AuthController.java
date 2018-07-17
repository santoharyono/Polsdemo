package com.mitrais.polsdemo.controller;

import com.mitrais.polsdemo.Exception.AppException;
import com.mitrais.polsdemo.model.Role;
import com.mitrais.polsdemo.model.RoleName;
import com.mitrais.polsdemo.model.User;
import com.mitrais.polsdemo.payload.response.ApiResponse;
import com.mitrais.polsdemo.payload.response.JwtAuthenticationResponse;
import com.mitrais.polsdemo.payload.request.LoginRequest;
import com.mitrais.polsdemo.payload.request.SignupRequest;
import com.mitrais.polsdemo.repository.RoleRepository;
import com.mitrais.polsdemo.repository.UserRepository;
import com.mitrais.polsdemo.security.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@Api(value = "authentication", description = "Authentication for access API")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/signin")
    @ApiOperation(value = "get token based on registered user")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        return  ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    @ApiOperation(value = "User signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
        }

//        create user
        User user = new User(signupRequest.getName(), signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("Role not set"));
        user.setRoles(Collections.singleton(role));

        User result = userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
