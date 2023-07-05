package com.lppaik.service;

import com.lppaik.entity.Jurusan;
import com.lppaik.model.request.CreateJurusanRequest;
import com.lppaik.model.response.JurusanResponse;

public interface JurusanService {

  void create(CreateJurusanRequest request);

  JurusanResponse jurusanToJurusanResponse(Jurusan jurusan);

  Jurusan getJurusanById(String id);
}
