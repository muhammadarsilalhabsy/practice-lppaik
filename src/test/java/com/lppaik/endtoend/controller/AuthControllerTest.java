package com.lppaik.endtoend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.Gender;
import com.lppaik.entity.Jurusan;
import com.lppaik.entity.Role;
import com.lppaik.entity.User;
import com.lppaik.model.response.ErrorResponse;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.LoginUserRequest;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.response.TokenResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  private final JurusanRepository jurusanRepository;

  private final UserRepository userRepository;

  @Autowired
  public AuthControllerTest(ObjectMapper mapper, MockMvc mvc, JurusanRepository jurusanRepository, UserRepository userRepository) {
    this.mapper = mapper;
    this.mvc = mvc;
    this.jurusanRepository = jurusanRepository;
    this.userRepository = userRepository;
  }

  @BeforeEach
  void setUp() {

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
    mahasiswa.setGender(Gender.MALE);
    mahasiswa.setPassword(BCrypt.hashpw("secret123", BCrypt.gensalt()));
    mahasiswa.setToken("test-token");
    mahasiswa.setTokenExpiredAt(100000L);

    userRepository.save(mahasiswa);

  }

  @Test
  void testRegisterSuccess() throws Exception{

    // request body
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("219191"); // can be NIM or any ID, with maximum length 10
    request.setJurusanId("Jxxx1"); // this 'Jxxx1' references to 'TEKNIK' which is we already set in before each
    request.setName("Muhammad Arsil Alhabsy"); // should be full name or
    request.setEmail("arsil@gmail.com"); // should be well email format
    request.setGender("MALE"); // Gender only have 2 option (MALE or FEMALE)
    request.setPassword("secret123"); // password will encrypt by BCrypt class


    mvc.perform(
            post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK", response.getData()); // data should be return "OK"

      // find user with id from our request "219191"
      User userFromDB = userRepository.findById(request.getUsername()).orElse(null);

      assertNotNull(userFromDB); // check data from database should be not null

      // check if each column from database is sync from our field request
      assertEquals(request.getUsername(), userFromDB.getUsername());
      assertEquals(request.getName(), userFromDB.getName());
      assertEquals(Role.MAHASISWA, userFromDB.getRole()); // when user register, role should be Mahasiswa by default
      assertEquals(Gender.MALE, userFromDB.getGender());
      assertEquals(request.getEmail(), userFromDB.getEmail());
      assertEquals(request.getJurusanId(), userFromDB.getJurusan().getId());
      assertTrue(BCrypt.checkpw(request.getPassword(), userFromDB.getPassword())); // password should be in BCrypt format


    });
  }

  @Test
  void testLoginSuccess() throws Exception{

    // request body (user should be registered first)
    // and sent his username and password
    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test-id");
    request.setPassword("secret123");


    mvc.perform(
            post("/api/v1/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<TokenResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      // user should be get a token and token expired
      assertNotNull(response.getData().getToken());
      assertNotNull(response.getData().getExpiredAt()); // token expiredAt will return milliseconds (30 days) expired
    });
  }

  @Test
  void testLogoutSuccess() throws Exception{

    mvc.perform(
            delete("/api/v1/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test-token") // user should sent X-API-TOKEN
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("OK", response.getData());

      User userFromDB = userRepository.findById("test-id").orElseThrow();
      // when logout, token and token expired should be null
      assertNull(userFromDB.getToken());
      assertNull(userFromDB.getTokenExpiredAt());
    });
  }

  @Test
  void testLogoutUnauthorized() throws Exception{

    // when user is not sent header 'X-API-TOKEN', response should be Error: Unauthorized
    mvc.perform(
            delete("/api/v1/auth/logout")
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

}
