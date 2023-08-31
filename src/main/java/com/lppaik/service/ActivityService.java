package com.lppaik.service;


import com.lppaik.entity.User;
import com.lppaik.model.request.*;
import com.lppaik.model.response.ActivityResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface ActivityService {

  void create(User user, CreateActivityRequest request) throws IOException;


  void addActivityToMahasiswa(User user, AddActivityToUserRequest request); // admin

  void update(User user, UpdateActivityRequest request) throws IOException;

  void delete(User user, String activityId);
  Page<ActivityResponse> searchActivity(SearchActivityRequest request);
}
