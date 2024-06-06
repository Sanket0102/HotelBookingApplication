package com.example.HotelLakeShore.controller;

import com.example.HotelLakeShore.exception.UserAlreadyExistsException;
import com.example.HotelLakeShore.model.User;
import com.example.HotelLakeShore.request.LoginRequest;
import com.example.HotelLakeShore.response.JwtResponse;
import com.example.HotelLakeShore.security.jwt.JWTUtils;
import com.example.HotelLakeShore.security.user.HotelUserDetails;
import com.example.HotelLakeShore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    public UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JWTUtils jwtUtils;


    @PostMapping("/register-user")
    public ResponseEntity<?> registeredUser(@RequestBody User user){
        try{
           userService.registerUser(user);
           return ResponseEntity.ok("User Successfully Registered");
        }
        catch(UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
       Authentication authentication =
               authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
       SecurityContextHolder.getContext().setAuthentication(authentication);
       String jwt = jwtUtils.generateJwtTokenForUser(authentication);

       // Current Loggen in User
       HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
       List<String> roles = userDetails.getAuthorities()
               .stream()
               .map(GrantedAuthority::getAuthority)
               .toList();
       return ResponseEntity.ok(new JwtResponse(userDetails.getId(),userDetails.getEmail(),jwt,roles));
    }
}
