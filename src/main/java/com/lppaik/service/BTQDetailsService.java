package com.lppaik.service;

import com.lppaik.entity.User;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.request.UpdateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;

import java.util.List;

public interface BTQDetailsService {

  List<BTQResponse> getDetails(User user, String bookId);

  void createForMahasiswa(User user, CreateBTQDetailsRequest request, String forMahasiswaId);

  void delete(User user, Long detailId);

  BTQResponse update(User user, UpdateBTQDetailsRequest request);

}
