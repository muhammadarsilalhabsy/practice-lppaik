package com.lppaik.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.*;
import com.lppaik.model.ErrorResponse;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.request.LoginUserRequest;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.model.response.TokenResponse;
import com.lppaik.repository.BTQControlBookRepository;
import com.lppaik.repository.BTQDetailsRepository;
import com.lppaik.repository.JurusanRepository;
import com.lppaik.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BTQControllerTest {

  private final UserRepository userRepository;
  private final JurusanRepository jurusanRepository;

  private final BTQDetailsRepository detailsRepository;

  private final BTQControlBookRepository controlBookRepository;

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  @Autowired
  public BTQControllerTest(UserRepository userRepository, JurusanRepository jurusanRepository, BTQDetailsRepository detailsRepository, BTQControlBookRepository controlBookRepository, ObjectMapper mapper, MockMvc mvc) {
    this.userRepository = userRepository;
    this.jurusanRepository = jurusanRepository;
    this.detailsRepository = detailsRepository;
    this.controlBookRepository = controlBookRepository;
    this.mapper = mapper;
    this.mvc = mvc;
  }

  @BeforeEach
  void setUp() {
    detailsRepository.deleteAll();
    controlBookRepository.deleteAll();
    userRepository.deleteAll();
    jurusanRepository.deleteAll();

    Jurusan jurusan = new Jurusan();
    jurusan.setName("PTI");
    jurusan.setId("J0001");
    jurusanRepository.save(jurusan);

    User request = new User();
    request.setName("Arsil");
    request.setEmail("arsil@gmail.com");
    request.setUsername("21916060");
    request.setPassword("rahasia");
    request.setToken("token ta");
    request.setJurusan(jurusan);
    request.setGender(Gender.MALE);

    userRepository.save(request);

    BTQControlBook book = new BTQControlBook();
    book.setId("21916060");
    book.setStatus(false);

    controlBookRepository.save(book);
  }

  @Test
  void testCreateDetailsSuccess() throws Exception{

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/21916060/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "token ta")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<BTQResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("belajar java", response.getData().getActivity());
      assertEquals("RABU", response.getData().getDay());
    });
  }

  @Test
  void testCreateDetailsUnauthorized() throws Exception{

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/21916060/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized", response.getError());
    });
  }

  @Test
  void testCreateDetailsBookNotFound() throws Exception{

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/21916060x/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "token ta")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("BTQ Book not found!", response.getError());
    });
  }


  @Test
  void testGetAllDetailsSuccess() throws Exception{

    BTQControlBook book = controlBookRepository.findById("21916060")
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    for (int i = 0; i < 10; i++) {
      BTQDetails details = new BTQDetails();
      details.setDay("day " + i);
      details.setActivity("belajar " + i);
      details.setBook(book);
      detailsRepository.save(details);
    }

    mvc.perform(
            get("/api/v1/btq/21916060/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "token ta")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<BTQResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(10,response.getData().size());
    });
  }
//  select u.name, b.status, d.day, d.activity from users as u
//    -> join buku_c_btq as b on (b.id=u.username)
//    -> join btq_details as d on (d.buku_id= b.id);

  @Test
  void testGetAllDetailsUnauthorized() throws Exception{

    BTQControlBook book = controlBookRepository.findById("21916060")
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    for (int i = 0; i < 10; i++) {
      BTQDetails details = new BTQDetails();
      details.setDay("day " + i);
      details.setActivity("belajar " + i);
      details.setBook(book);
      detailsRepository.save(details);
    }

    mvc.perform(
            get("/api/v1/btq/21916060/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized", response.getError());
    });
  }

  @Test
  void testGetAllDetailsBookNotFound() throws Exception{

    BTQControlBook book = controlBookRepository.findById("21916060")
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    for (int i = 0; i < 10; i++) {
      BTQDetails details = new BTQDetails();
      details.setDay("day " + i);
      details.setActivity("belajar " + i);
      details.setBook(book);
      detailsRepository.save(details);
    }

    mvc.perform(
            get("/api/v1/btq/21916060x/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "token ta")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("BTQ Book not found!", response.getError());
    });
  }

}