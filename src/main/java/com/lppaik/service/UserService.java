package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.request.SearchUserRequest;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.model.response.UserResponse;
import org.springframework.data.domain.Page;


public interface UserService {

  void register(RegisterUserRequest request);

  UserResponse getCurrentUser(User user);

  Page<UserResponse> searchUser(SearchUserRequest request);

  UserResponse updateCurrentUser(User user, UpdateUserRequest request);

}
