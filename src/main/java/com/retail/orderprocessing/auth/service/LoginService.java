package com.retail.orderprocessing.auth.service;

import com.retail.orderprocessing.auth.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {

  @Value("${app.auth.token}")
  private String token;

  @Value("${app.auth.username}")
  private String username;

  @Value("${app.auth.password}")
  private String password;

  public String login(User user) {
    if (username.equalsIgnoreCase(user.getUsername())
        && password.equalsIgnoreCase(user.getPassword())) {
      return token;
    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid User Credentials");
    }
  }
}
