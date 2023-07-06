package com.lppaik.service;

import com.lppaik.entity.Jurusan;
import com.lppaik.entity.Role;
import com.lppaik.entity.User;
import com.lppaik.model.request.CreateJurusanRequest;
import com.lppaik.model.request.UpdateJurusanRequest;
import com.lppaik.model.response.JurusanResponse;
import com.lppaik.repository.JurusanRepository;
import com.lppaik.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class JurusanServiceImpl implements JurusanService {
  private final JurusanRepository repository;
  private final Utils utils;

  private final UserRepository userRepository;

  public JurusanServiceImpl(JurusanRepository repository, Utils utils, UserRepository userRepository) {
    this.repository = repository;
    this.utils = utils;
    this.userRepository = userRepository;
  }

  @Override
  public void create(User user, CreateJurusanRequest request) {

    utils.validate(request);

    if(user.getRole() != Role.TUTOR && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }


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

  @Override
  public List<JurusanResponse> getAll() {
    List<Jurusan> jurusans = repository.findAll();

    return jurusans.stream()
            .map(this::jurusanToJurusanResponse)
            .toList();
  }

  @Override
  public void delete(User user, String jurusanId) {

    if(user.getRole() != Role.TUTOR && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    Jurusan jurusan = repository.findById(jurusanId)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jurusan Not Found!"));

    repository.delete(jurusan);
  }

  @Override
  public JurusanResponse update(User user, UpdateJurusanRequest request) {

    utils.validate(request);

    if(user.getRole() != Role.TUTOR && user.getRole() != Role.ADMIN){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Operation is not support for you role!");
    }

    Jurusan jurusan = repository.findById(request.getId())
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jurusan Not Found!"));

    if(Objects.nonNull(request.getName())){
      jurusan.setName(request.getName());
    }

    repository.save(jurusan);

    return jurusanToJurusanResponse(jurusan);
  }
}
