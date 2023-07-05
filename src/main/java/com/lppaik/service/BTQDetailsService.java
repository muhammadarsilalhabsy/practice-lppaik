package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;

import java.util.List;

public interface BTQDetailsService {

  List<BTQResponse> getDetails(User user, String bookId);

  BTQResponse create(User user, CreateBTQDetailsRequest request);
}
