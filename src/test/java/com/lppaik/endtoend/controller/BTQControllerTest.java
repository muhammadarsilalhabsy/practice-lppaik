package com.lppaik.endtoend.controller;

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
import com.lppaik.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class BTQControllerTest {

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  private final JurusanRepository jurusanRepository;

  private final UserRepository userRepository;

  private final BTQDetailsRepository btqDetailsRepository;

  private final BTQControlBookRepository bookRepository;

  @Autowired
  public BTQControllerTest(ObjectMapper mapper, MockMvc mvc, JurusanRepository jurusanRepository, UserRepository userRepository, BTQDetailsRepository repository, BTQControlBookRepository bookRepository) {
    this.mapper = mapper;
    this.mvc = mvc;
    this.jurusanRepository = jurusanRepository;
    this.userRepository = userRepository;
    this.btqDetailsRepository = repository;
    this.bookRepository = bookRepository;
  }

  @BeforeEach
  void setUp() {

    btqDetailsRepository.deleteAll();
    bookRepository.deleteAll();
    userRepository.deleteAll();
    jurusanRepository.deleteAll();

    /*
     * First create jurusan, for user register
     * */
    Jurusan jurusan = new Jurusan();
    jurusan.setId("Jxxx1");
    jurusan.setName("TEKNIK");

    jurusanRepository.save(jurusan);

    User mahasiswa = new User();
    mahasiswa.setUsername("test-id");
    mahasiswa.setJurusan(jurusan);
    mahasiswa.setName("Otong");
    mahasiswa.setEmail("otong@gmail.com");
    mahasiswa.setRole(Role.MAHASISWA);
    mahasiswa.setGender(Gender.MALE);
    mahasiswa.setPassword(BCrypt.hashpw("secret123", BCrypt.gensalt()));
    mahasiswa.setToken("test-token");
    mahasiswa.setTokenExpiredAt(100000L);

    userRepository.save(mahasiswa);

    BTQControlBook book = new BTQControlBook();
    book.setStatus(false);
    book.setId("test-id");

    bookRepository.save(book);

    User tutor = new User();
    tutor.setUsername("tutor-id");
    tutor.setJurusan(jurusan);
    tutor.setName("Jamal");
    tutor.setEmail("jamal@gmail.com");
    tutor.setRole(Role.TUTOR);
    tutor.setGender(Gender.MALE);
    tutor.setPassword(BCrypt.hashpw("secret123", BCrypt.gensalt()));
    tutor.setToken("tutor-token");
    tutor.setTokenExpiredAt(100000L);

    userRepository.save(tutor);

    User dosen = new User();
    dosen.setUsername("dosen-id");
    dosen.setJurusan(jurusan);
    dosen.setName("rudi");
    dosen.setEmail("rudi@gmail.com");
    dosen.setRole(Role.DOSEN);
    dosen.setGender(Gender.MALE);
    dosen.setPassword(BCrypt.hashpw("secret123", BCrypt.gensalt()));
    dosen.setToken("rudi-token");
    dosen.setTokenExpiredAt(100000L);

    userRepository.save(dosen);


  }

  @Test
  void testUpdateDetailBTQToUserSuccess() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    BTQDetails details = new BTQDetails();
    details.setTutors(tutor);
    details.setActivity("IQRO hal 23 - 25");
    details.setBook(book);
    details.setDay("JUMAT");

    btqDetailsRepository.save(details);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("KAMIS");
    request.setActivity("IQRO hal 24 - 26");


    log.info("id = {}", details.getId());

    mvc.perform(
            patch("/api/v1/btq/tutor/" + details.getId() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", tutor.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<BTQResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(tutor.getName(), response.getData().getTutor());
      assertEquals(request.getActivity(), response.getData().getActivity());
      assertEquals(request.getDay(), response.getData().getDay());

    });
  }

  @Test
  void testUpdateDetailBTQToUserUnauthorized() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    BTQDetails details = new BTQDetails();
    details.setTutors(tutor);
    details.setActivity("IQRO hal 23 - 25");
    details.setBook(book);
    details.setDay("JUMAT");

    btqDetailsRepository.save(details);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("KAMIS");
    request.setActivity("IQRO hal 24 - 26");

    mvc.perform(
            patch("/api/v1/btq/tutor/" + details.getId() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized", response.getError());

    });
  }

  @Test
  void testUpdateDetailBTQToUserErrorBecauseRole() throws Exception{

    User dosen = userRepository.findById("dosen-id").orElseThrow();
    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    BTQDetails details = new BTQDetails();
    details.setTutors(tutor);
    details.setActivity("IQRO hal 23 - 25");
    details.setBook(book);
    details.setDay("JUMAT");

    btqDetailsRepository.save(details);

    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("KAMIS");
    request.setActivity("IQRO hal 24 - 26");

    mvc.perform(
            patch("/api/v1/btq/tutor/" + details.getId() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", dosen.getToken())
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Operation is not support for you role!", response.getError());

    });
  }

  @Test
  void testUpdateDetailBTQToUserNotFound() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();


    UpdateBTQDetailsRequest request = new UpdateBTQDetailsRequest();
    request.setDay("KAMIS");
    request.setActivity("IQRO hal 24 - 26");

    int wrongDetailId = 1000;

    mvc.perform(
            delete("/api/v1/btq/tutor/" + wrongDetailId + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", tutor.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("BTQ detail not found!", response.getError());

    });
  }
  @Test
  void testDeleteDetailBTQToUserSuccess() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    BTQDetails details = new BTQDetails();
    details.setTutors(tutor);
    details.setActivity("IQRO hal 23 - 25");
    details.setBook(book);
    details.setDay("JUMAT");

    btqDetailsRepository.save(details);

    mvc.perform(
            delete("/api/v1/btq/tutor/" + details.getId() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", tutor.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK", response.getData());

    });
  }

  @Test
  void testDeleteDetailBTQToUserUnauthorized() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    BTQDetails details = new BTQDetails();
    details.setTutors(tutor);
    details.setActivity("IQRO hal 23 - 25");
    details.setBook(book);
    details.setDay("JUMAT");

    btqDetailsRepository.save(details);

    mvc.perform(
            delete("/api/v1/btq/tutor/" + details.getId() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized", response.getError());

    });
  }

  @Test
  void testDeleteDetailBTQToUserErrorBecauseRole() throws Exception{

    User dosen = userRepository.findById("dosen-id").orElseThrow();
    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    BTQDetails details = new BTQDetails();
    details.setTutors(tutor);
    details.setActivity("IQRO hal 23 - 25");
    details.setBook(book);
    details.setDay("JUMAT");

    btqDetailsRepository.save(details);

    mvc.perform(
            delete("/api/v1/btq/tutor/" + details.getId() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", dosen.getToken())
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Operation is not support for you role!", response.getError());

    });
  }

  @Test
  void testDeleteDetailBTQToUserNotFound() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();


    int wrongDetailId = 1000;

    mvc.perform(
            delete("/api/v1/btq/tutor/" + wrongDetailId + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", tutor.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("BTQ detail not found!", response.getError());

    });
  }

  @Test
  void testAddDetailBTQToUserSuccess() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("IQRO Hal 1 - 5");
    request.setDay("KAMIS"); // sementara

    mvc.perform(
            post("/api/v1/btq/tutor/" + mahasiswa.getUsername() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", tutor.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK", response.getData());

    });
  }

  @Test
  void testAddDetailBTQToUserUnauthorized() throws Exception{

    User mahasiswa = userRepository.findById("test-id").orElseThrow();

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("IQRO Hal 1 - 5");
    request.setDay("KAMIS"); // sementara

    mvc.perform(
            post("/api/v1/btq/tutor/" + mahasiswa.getUsername() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized", response.getError());

    });
  }

  @Test
  void testAddDetailBTQToUserErrorBecauseRole() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("IQRO Hal 1 - 5");
    request.setDay("KAMIS"); // sementara

    mvc.perform(
            post("/api/v1/btq/tutor/" + tutor.getUsername() + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", mahasiswa.getToken())
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Operation is not support for you role!", response.getError());

    });
  }

  @Test
  void testAddDetailBTQToUserNotFound() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();

    CreateBTQDetailsRequest request = new CreateBTQDetailsRequest();
    request.setActivity("IQRO Hal 1 - 5");
    request.setDay("KAMIS"); // sementara

    String wrongId = "wrong-id";

    mvc.perform(
            post("/api/v1/btq/tutor/" + wrongId + "/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", tutor.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Mahasiswa with id " + wrongId + " is not found!", response.getError());

    });
  }


}
