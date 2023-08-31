package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.request.AddActivityToUserRequest;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.request.SearchUserRequest;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.model.response.UserActivityResponse;
import com.lppaik.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface UserService {

  void register(RegisterUserRequest request);

  UserResponse getCurrentUser(User user);

  Page<UserResponse> searchUser(User user,SearchUserRequest request); // tutor, admin, dosen

  UserResponse updateCurrentUser(User user, UpdateUserRequest request);

  List<UserActivityResponse> getUserActivities(User user);

  List<BTQResponse> getBTQDetails(User user);

  void updateUserAvatar(User user, MultipartFile file) throws IOException;

}
