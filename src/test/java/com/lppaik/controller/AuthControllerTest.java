package com.lppaik.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.model.response.ErrorResponse;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.LoginUserRequest;
import com.lppaik.model.response.TokenResponse;
import com.lppaik.repository.JurusanRepository;
import com.lppaik.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  private final UserRepository userRepository;
  private final JurusanRepository jurusanRepository;

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  @Autowired
  public AuthControllerTest(UserRepository userRepository, JurusanRepository jurusanRepository, ObjectMapper mapper, MockMvc mvc) {
    this.userRepository = userRepository;
    this.jurusanRepository = jurusanRepository;
    this.mapper = mapper;
    this.mvc = mvc;
  }

  @Test
  void testLoginSuccess() throws Exception{

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("007");
    request.setPassword("rahasia");

    mvc.perform(
            post("/api/v1/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<TokenResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData().getToken());
      assertNotNull(response.getData().getExpiredAt());
    });
  }

  @Test
  void testLoginFailed() throws Exception{

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("21916060");
    request.setPassword("xxxx");

    mvc.perform(
            post("/api/v1/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getError());
    });
  }

  @Test
  void testLogoutSuccess() throws Exception{


    mvc.perform(
            delete("/api/v1/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "8536083e-0178-4d2e-a9db-49c6d49b94d1")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData());
      assertEquals("OK", response.getData());
    });

  }

  @Test
  void testLogoutFailed() throws Exception {

    mvc.perform(
            delete("/api/v1/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getError());
      assertEquals("Unauthorized", response.getError());
    });
  }
}