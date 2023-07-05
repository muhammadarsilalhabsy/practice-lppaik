package com.lppaik.service;

import com.lppaik.entity.Jurusan;
import com.lppaik.model.request.CreateJurusanRequest;
import com.lppaik.model.response.JurusanResponse;
import com.lppaik.repository.JurusanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JurusanServiceImpl implements JurusanService {
  private final JurusanRepository repository;
  private final Utils utils;

  public JurusanServiceImpl(JurusanRepository repository, Utils utils) {
    this.repository = repository;
    this.utils = utils;
  }

  @Override
  public void create(CreateJurusanRequest request) {
    utils.validate(request);

    if(repository.existsById(request.getId())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jurusan Already exsist!");
    }

    Jurusan jurusan = new Jurusan();
    jurusan.setId(request.getId());
    jurusan.setName(request.getName());

    repository.save(jurusan);
  }

  @Override
  public JurusanResponse jurusanToJurusanResponse(Jurusan jurusan) {
    return JurusanResponse.builder().id(jurusan.getId()).name(jurusan.getName()).build();
  }

  @Override
  public Jurusan getJurusanById(String id) {
    return repository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jurusan Not Found!"));
  }
}
