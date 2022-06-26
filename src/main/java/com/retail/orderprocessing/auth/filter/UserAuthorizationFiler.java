package com.retail.orderprocessing.auth.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserAuthorizationFiler implements Filter {

  @Value("${app.auth.header}")
  private String headerName;

  @Value("${app.auth.token}")
  private String token;

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    if (request.getRequestURI().equalsIgnoreCase("/v1/login") || checkToken(request)) {
      filterChain.doFilter(request, response);
    }
    response.setStatus(HttpStatus.FORBIDDEN.value());
  }

  private boolean checkToken(HttpServletRequest request) {
    String authenticationHeader = request.getHeader(headerName);
    return authenticationHeader != null && authenticationHeader.equalsIgnoreCase(token);
  }
}
