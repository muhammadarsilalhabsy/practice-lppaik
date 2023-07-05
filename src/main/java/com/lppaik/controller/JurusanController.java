package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.CreateJurusanRequest;
import com.lppaik.model.response.JurusanResponse;
import com.lppaik.service.JurusanServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/jurusan")
public class JurusanController {

  private final JurusanServiceImpl service;

  public JurusanController(JurusanServiceImpl service) {
    this.service = service;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<JurusanResponse>> getAll(){

    List<JurusanResponse> responses = service.getAll();

    return WebResponse.<List<JurusanResponse>>builder()
            .data(responses)
            .build();
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> create(User user, @RequestBody CreateJurusanRequest request){

    service.create(user, request);

    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }
}
