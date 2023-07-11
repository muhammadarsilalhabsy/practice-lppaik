package com.lppaik.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.Gender;
import com.lppaik.entity.Jurusan;
import com.lppaik.entity.Role;
import com.lppaik.entity.User;
import com.lppaik.model.response.ErrorResponse;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.CreateJurusanRequest;
import com.lppaik.model.request.UpdateJurusanRequest;
import com.lppaik.model.response.JurusanResponse;
import com.lppaik.repository.JurusanRepository;
import com.lppaik.repository.UserRepository;
import com.lppaik.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JurusanControllerTest {

  private final JurusanRepository jurusanRepository;

  private final UserRepository repository;

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  @Autowired
  public JurusanControllerTest(JurusanRepository jurusanRepository, UserRepository repository, ObjectMapper mapper, MockMvc mvc) {
    this.jurusanRepository = jurusanRepository;
    this.repository = repository;
    this.mapper = mapper;
    this.mvc = mvc;
  }


  @BeforeEach
  void setUp() {
    repository.deleteAll();
    jurusanRepository.deleteAll();

    Jurusan j1 = new Jurusan();
    j1.setId("j1");
    j1.setName("PTI");
    jurusanRepository.save(j1);

    Jurusan j2 = new Jurusan();
    j2.setId("j2");
    j2.setName("PAUD");
    jurusanRepository.save(j2);

    Jurusan j3 = new Jurusan();
    j3.setId("j3");
    j3.setName("BHI");
    jurusanRepository.save(j3);

    User user = new User();
    user.setName("Arsil");
    user.setEmail("arsil@gmail.com");
    user.setUsername("008");
    user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
    user.setRole(Role.MAHASISWA);
    user.setGender(Gender.FEMALE);
    user.setToken("token ku");
    user.setJurusan(j1);
    user.setTokenExpiredAt(1000000L);
    repository.save(user);
  }

  @Test
  void createSuccess() throws Exception {

    User admin = new User();
    admin.setName("Ucok");
    admin.setEmail("admin@gmail.com");
    admin.setUsername("1010");
    admin.setPassword("rahasia");
    admin.setToken("admin-token");
    admin.setGender(Gender.MALE);
    admin.setRole(Role.ADMIN);

    repository.save(admin);

    CreateJurusanRequest request = new CreateJurusanRequest();
    request.setName("TEKNIK");
    request.setId("j4");

    mvc.perform(
                    post("/api/v1/jurusan")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
                            .header("X-API-TOKEN", "admin-token"))
            .andExpectAll(
                    status().isOk())
            .andDo(result -> {
              WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertNotNull(response.getData());
              assertEquals("OK", response.getData());

              Jurusan fromDb = jurusanRepository.findById("j4").orElse(null);
              assertNotNull(fromDb);
              assertEquals("TEKNIK", fromDb.getName());

            });
  }

  @Test
  void createUnauthorized() throws Exception {

    CreateJurusanRequest request = new CreateJurusanRequest();
    request.setName("TEKNIK");
    request.setId("j4");

    mvc.perform(
                    post("/api/v1/jurusan")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
            .andExpectAll(
                    status().isUnauthorized())
            .andDo(result -> {
              ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertNotNull(response.getError());
              assertEquals("Unauthorized", response.getError());

            });
  }

  @Test
  void createFailedBecauseRole() throws Exception {

    CreateJurusanRequest request = new CreateJurusanRequest();
    request.setName("TEKNIK");
    request.setId("j4");

    mvc.perform(
                    post("/api/v1/jurusan")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
                            .header("X-API-TOKEN", "token ku"))

            .andExpectAll(
                    status().isUnauthorized())
            .andDo(result -> {
              ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertNotNull(response.getError());
              assertEquals("Operation is not support for you role!", response.getError());

            });
  }

  @Test
  void getAll() throws Exception {

    mvc.perform(
                    get("/api/v1/jurusan")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                    status().isOk())
            .andDo(result -> {
              WebResponse<List<JurusanResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertNotNull(response.getData());
              assertEquals(3, response.getData().size());

            });
  }

  @Test
  void deleteSuccess() throws Exception {

    User admin = new User();
    admin.setName("Ucok");
    admin.setEmail("admin@gmail.com");
    admin.setUsername("1010");
    admin.setPassword("rahasia");
    admin.setToken("admin-token");
    admin.setGender(Gender.MALE);
    admin.setRole(Role.TUTOR);

    repository.save(admin);

    mvc.perform(
                    delete("/api/v1/jurusan/j2")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-TOKEN", "admin-token"))
            .andExpectAll(
                    status().isOk())
            .andDo(result -> {
              WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertNotNull(response.getData());
              assertEquals("OK", response.getData());

            });
  }

  @Test
  void deleteFailedBecauseRole() throws Exception {

    User ketua = new User();
    ketua.setName("Ucok");
    ketua.setEmail("ketua@gmail.com");
    ketua.setUsername("1010");
    ketua.setPassword("rahasia");
    ketua.setToken("ketua-token");
    ketua.setGender(Gender.MALE);
    ketua.setRole(Role.KETUA);

    repository.save(ketua);

    mvc.perform(
                    delete("/api/v1/jurusan/j2")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-TOKEN", "ketua-token"))
            .andExpectAll(
                    status().isUnauthorized())
            .andDo(result -> {
              ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertEquals("Operation is not support for you role!", response.getError());

            });
  }

  @Test
  void deleteJurusanNotFound() throws Exception {

    User admin = new User();
    admin.setName("Ucok");
    admin.setEmail("admin@gmail.com");
    admin.setUsername("1010");
    admin.setPassword("rahasia");
    admin.setToken("admin-token");
    admin.setGender(Gender.MALE);
    admin.setRole(Role.TUTOR);

    repository.save(admin);

    mvc.perform(
                    delete("/api/v1/jurusan/j5")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-TOKEN", "admin-token"))
            .andExpectAll(
                    status().isNotFound())
            .andDo(result -> {
              ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertEquals("Jurusan Not Found!", response.getError());

            });
  }

  @Test
  void updateSuccess() throws Exception {

    User admin = new User();
    admin.setName("Ucok");
    admin.setEmail("admin@gmail.com");
    admin.setUsername("1010");
    admin.setPassword("rahasia");
    admin.setToken("admin-token");
    admin.setGender(Gender.MALE);
    admin.setRole(Role.TUTOR);

    repository.save(admin);

    UpdateJurusanRequest request = new UpdateJurusanRequest();
    request.setName("PERKAPALAN");

    mvc.perform(
                    patch("/api/v1/jurusan/j2")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-TOKEN", "admin-token")
                            .content(mapper.writeValueAsString(request)))
            .andExpectAll(
                    status().isOk())
            .andDo(result -> {
              WebResponse<JurusanResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });

              assertNotNull(response.getData());
              assertEquals("PERKAPALAN", response.getData().getName());

            });
  }

  @Test
  void updateFailedBecauseRole() throws Exception {

    User ketua = new User();
    ketua.setName("Ucok");
    ketua.setEmail("ketua@gmail.com");
    ketua.setUsername("1010");
    ketua.setPassword("rahasia");
    ketua.setToken("ketua-token");
    ketua.setGender(Gender.MALE);
    ketua.setRole(Role.KETUA);

    repository.save(ketua);

    UpdateJurusanRequest request = new UpdateJurusanRequest();
    request.setName("PERKAPALAN");

    mvc.perform(
              patch("/api/v1/jurusan/j2")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "ketua-token")
                      .content(mapper.writeValueAsString(request)))
      .andExpectAll(
              status().isUnauthorized())
      .andDo(result -> {
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Operation is not support for you role!", response.getError());

      });
  }

  @Test
  void updateUnauthorized() throws Exception {

    UpdateJurusanRequest request = new UpdateJurusanRequest();
    request.setName("PERKAPALAN");

    mvc.perform(
              patch("/api/v1/jurusan/j2")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(mapper.writeValueAsString(request)))
      .andExpectAll(
              status().isUnauthorized())
      .andDo(result -> {
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Unauthorized", response.getError());

      });
  }

  @Test
  void updateNotFound() throws Exception {
    User admin = new User();
    admin.setName("Ucok");
    admin.setEmail("admin@gmail.com");
    admin.setUsername("1010");
    admin.setPassword("rahasia");
    admin.setToken("admin-token");
    admin.setGender(Gender.MALE);
    admin.setRole(Role.TUTOR);

    repository.save(admin);

    UpdateJurusanRequest request = new UpdateJurusanRequest();
    request.setName("PERKAPALAN");

    mvc.perform(
              patch("/api/v1/jurusan/jxxx")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(mapper.writeValueAsString(request))
                      .header("X-API-TOKEN", "admin-token"))
      .andExpectAll(
              status().isNotFound())
      .andDo(result -> {
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Jurusan Not Found!", response.getError());

      });
  }

}