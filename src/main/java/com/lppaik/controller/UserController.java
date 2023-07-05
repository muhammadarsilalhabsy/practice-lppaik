package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.request.SearchUserRequest;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.model.response.PagingResponse;
import com.lppaik.model.response.UserResponse;
import com.lppaik.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

  private final UserServiceImpl service;

  @Autowired
  public UserController(UserServiceImpl service) {
    this.service = service;
  }

  @PostMapping(path = "/register",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> register(@RequestBody RegisterUserRequest request){

    service.register(request);

    return WebResponse.<String>builder().data("OK").build();
  };

  @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> getCurrentUser(User user){
    UserResponse response = service.getCurrentUser(user);
    return WebResponse.<UserResponse>builder()
            .data(response)
            .build();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<UserResponse>> searchUser(@RequestParam(value = "identity", required = false) String identity,
                                                    @RequestParam(value = "jurusan", required = false) String jurusan,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){

    SearchUserRequest request = SearchUserRequest.builder()
            .jurusan(jurusan)
            .identity(identity)
            .page(page)
            .size(size)
            .build();

    Page<UserResponse> userResponses = service.searchUser(request);
    return WebResponse.<List<UserResponse>>builder()
            .data(userResponses.getContent())
            .paging(PagingResponse.builder()
                    .currentPage(userResponses.getNumber())
                    .totalPage(userResponses.getTotalPages())
                    .size(userResponses.getSize())
                    .build())
            .build();
  }

  @PatchMapping(path = "/current",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> updateCurrentUser(User user, @RequestBody UpdateUserRequest request){
    UserResponse response = service.updateCurrentUser(user, request);
    return WebResponse.<UserResponse>builder()
            .data(response)
            .build();
  }
}
