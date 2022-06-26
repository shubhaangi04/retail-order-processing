package com.retail.orderprocessing.auth.controller;

import com.retail.orderprocessing.auth.model.User;
import com.retail.orderprocessing.auth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/login")
public class LoginController {

  private final LoginService loginService;

  @Autowired
  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  @PostMapping
  ResponseEntity<String> login(@Valid @RequestBody User user) {
    return new ResponseEntity<>(loginService.login(user), HttpStatus.OK);
  }
}
