package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.request.LoginUserRequest;
import com.lppaik.model.response.TokenResponse;


public interface AuthService {
  TokenResponse login(LoginUserRequest request);
  void logout(User user);
}
