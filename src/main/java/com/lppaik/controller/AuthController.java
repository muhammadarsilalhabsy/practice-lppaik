package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.LoginUserRequest;
import com.lppaik.model.response.TokenResponse;
import com.lppaik.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

  @Autowired
  private AuthServiceImpl authServiceImpl;

  @PostMapping(path = "/login",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
    TokenResponse tokenResponse = authServiceImpl.login(request);

    return WebResponse.<TokenResponse>builder()
            .data(tokenResponse)
            .build();
  }

  @DeleteMapping(
          path = "/logout",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> logout(User user){
    authServiceImpl.logout(user);
    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }
}
