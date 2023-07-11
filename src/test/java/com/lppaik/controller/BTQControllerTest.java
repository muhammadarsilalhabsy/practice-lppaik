package com.lppaik.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.*;
import com.lppaik.model.response.ErrorResponse;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.request.UpdateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;
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

    User tutor = new User();
    tutor.setName("Umbi");
    tutor.setEmail("umbi@gmail.com");
    tutor.setUsername("9999");
    tutor.setPassword("rahasia");
    tutor.setToken("umbi-tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);
    userRepository.save(tutor);

    BTQDetails detail = new BTQDetails();
    detail.setDay("MINGGU");
    detail.setActivity("Berenang");
    detail.setTutors(tutor);
    detail.setBook(book);

    detailsRepository.save(detail);
  }

  @Test
  void testCreateDetailsSuccess() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    userRepository.save(tutor);

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/tutor/21916060/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK", response.getData());
    });
  }

  @Test
  void testCreateDetailsUnauthorized() throws Exception{

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/tutor/21916060/details")
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
  void testCreateDetailsUnauthorizedBecauseRoleNotAdminOrTutor() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.MAHASISWA);

    userRepository.save(tutor);

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/tutor/21916060/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Operation is not support for you role!", response.getError());
    });
  }

  @Test
  void testCreateDetailsBookNotFound() throws Exception {

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    userRepository.save(tutor);

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("belajar java");
    request.setDay("RABU");

    mvc.perform(
            post("/api/v1/btq/tutor/21xx/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Mahasiswa with id 21xx is not found!", response.getError());
    });
  }

  @Test
  void testGetAllDetailsSuccess() throws Exception{

    BTQControlBook book = controlBookRepository.findById("21916060")
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "BTQ Book not found!"));

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    User jamal = new User();
    jamal.setName("jamal");
    jamal.setEmail("jamal@gmail.com");
    jamal.setUsername("10991");
    jamal.setPassword("rahasia");
    jamal.setToken("jamal-token");
    jamal.setGender(Gender.MALE);
    jamal.setRole(Role.TUTOR);

    userRepository.saveAll(List.of(jamal, tutor));

    for (int i = 0; i < 10; i++) {
      BTQDetails details = new BTQDetails();
      details.setDay("day " + i);
      details.setActivity("belajar " + i);
      details.setBook(book);
      if(i % 2 == 0){
        details.setTutors(tutor);
      }else{
        details.setTutors(jamal);
      }
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

      assertEquals(11,response.getData().size());
    });
  }

  @Test
  void testGetAllDetailsUnauthorized() throws Exception{
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

  @Test
  void testDeleteUnauthorizedBecauseRoleNotAdminOrTutor() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.MAHASISWA);

    userRepository.save(tutor);

    mvc.perform(
            delete("/api/v1/btq/tutor/1/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Operation is not support for you role!", response.getError());
    });
  }

  @Test
  void testDeleteSuccess() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    userRepository.save(tutor);

    mvc.perform(
            delete("/api/v1/btq/tutor/1/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK", response.getData());
    });
  }

  @Test
  void testUpdateSuccess() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    userRepository.save(tutor);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("Harriii yang menyenangkan");
    request.setActivity("Belajaarrrr!");

    mvc.perform(
            patch("/api/v1/btq/tutor/"+1+"/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<BTQResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Harriii yang menyenangkan", response.getData().getDay());
      assertEquals("Belajaarrrr!", response.getData().getActivity());
      assertEquals("Ucok", response.getData().getTutor());
    });
  }

  @Test
  void testUpdateUnauthorized() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    userRepository.save(tutor);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("Harriii yang menyenangkan");
    request.setActivity("Belajaarrrr!");

    mvc.perform(
            patch("/api/v1/btq/tutor/1/details")
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
  void testUpdateUnauthorizedBecauseRoleNotAdminOrTutor() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.MAHASISWA);

    userRepository.save(tutor);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("Harriii yang menyenangkan");
    request.setActivity("Belajaarrrr!");

    mvc.perform(
            patch("/api/v1/btq/tutor/1/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Operation is not support for you role!", response.getError());
    });
  }

  @Test
  void testUpdateNotFound() throws Exception{

    User tutor = new User();
    tutor.setName("Ucok");
    tutor.setEmail("tutor@gmail.com");
    tutor.setUsername("1010");
    tutor.setPassword("rahasia");
    tutor.setToken("tutor-token");
    tutor.setGender(Gender.MALE);
    tutor.setRole(Role.TUTOR);

    userRepository.save(tutor);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("Harriii yang menyenangkan");
    request.setActivity("Belajaarrrr!");

    mvc.perform(
            patch("/api/v1/btq/tutor/2/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("BTQ detail not found!", response.getError());
    });
  }
}