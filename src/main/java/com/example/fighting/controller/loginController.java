package com.example.fighting.controller;

import com.example.fighting.JWT.JwtUtils;
import com.example.fighting.service.LoginService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class loginController {
    private AuthenticationManager authenticationManager;
    private LoginService loginService;
    public loginController(AuthenticationManager authenticationManager, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.loginService = loginService;
    }

    //SpringSecuroty登入後導向的位置
    @PostMapping("/login")
    public ResponseEntity<String> home(String username, String password) throws UsernameNotFoundException {
        loginService.loadUserByUsername(username);//錯誤會丟出例外
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String jwtToken = JwtUtils.generateToken(username);
        return  ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body("");
    }
}
