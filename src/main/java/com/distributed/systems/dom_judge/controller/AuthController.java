package com.distributed.systems.dom_judge.controller;

import com.distributed.systems.dom_judge.dto.GenericRestResponse;
import com.distributed.systems.dom_judge.dto.LoginDto;
import com.distributed.systems.dom_judge.dto.UserDto;
import com.distributed.systems.dom_judge.model.User;
import com.distributed.systems.dom_judge.security.JwtTokenUtil;
import com.distributed.systems.dom_judge.service.DomJudgeUserDetailsService;
import com.distributed.systems.dom_judge.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityExistsException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final DomJudgeUserDetailsService userDetailsService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          DomJudgeUserDetailsService userDetailsService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @RequestMapping(value = "/register/user", method = RequestMethod.POST)
    public ResponseEntity<GenericRestResponse> registerUser(@RequestBody UserDto authDto) {
        User user = userService.findByUsername(authDto.getUsername());
        if (user != null) if(user.isEnabled()) throw new EntityExistsException();
        userService.register(user, authDto, "ROLE_USER");
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(value = "/register/admin", method = RequestMethod.POST)
    public ResponseEntity<GenericRestResponse> registerAdmin(@RequestBody UserDto authDto) {
        User user = userService.findByUsername(authDto.getUsername());
        if (user != null) if(user.isEnabled()) throw new EntityExistsException();
        userService.register(user, authDto, "ROLE_ADMIN");
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS), HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<GenericRestResponse<LoginDto>> login(@RequestBody UserDto authDto) {
        authenticate(authDto.getUsername(), authDto.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authDto.getUsername());
        User user = userService.findByUsername(authDto.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        LoginDto res = new LoginDto(token, user.getRole().getName());
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS, res), HttpStatus.OK);
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
