package com.lppaik.service;

import com.lppaik.entity.Jurusan;
import com.lppaik.entity.User;
import com.lppaik.model.request.CreateJurusanRequest;
import com.lppaik.model.request.UpdateJurusanRequest;
import com.lppaik.model.response.JurusanResponse;

import java.util.List;

public interface JurusanService {

  void create(User user, CreateJurusanRequest request);

  JurusanResponse jurusanToJurusanResponse(Jurusan jurusan);

  Jurusan getJurusanById(String id);

  List<JurusanResponse> getAll();

  void delete(User user, String jurusanId);

  JurusanResponse update(User user, UpdateJurusanRequest request);
}
