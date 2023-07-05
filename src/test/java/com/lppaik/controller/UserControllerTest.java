package com.lppaik.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.Gender;
import com.lppaik.entity.Jurusan;
import com.lppaik.entity.Role;
import com.lppaik.entity.User;
import com.lppaik.model.ErrorResponse;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.RegisterUserRequest;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.model.response.UserResponse;
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
class UserControllerTest {
  private final UserRepository userRepository;
  private final JurusanRepository jurusanRepository;

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  @Autowired
  public UserControllerTest(UserRepository userRepository, JurusanRepository jurusanRepository, ObjectMapper mapper, MockMvc mvc) {
    this.userRepository = userRepository;
    this.jurusanRepository = jurusanRepository;
    this.mapper = mapper;
    this.mvc = mvc;
  }

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    jurusanRepository.deleteAll();
  }

  @Test
  void testRegister() throws Exception {
    Jurusan jurusan = new Jurusan();
    jurusan.setName("PTI");
    jurusan.setId("J0001");
    jurusanRepository.save(jurusan);

    RegisterUserRequest request = new RegisterUserRequest();
    request.setName("Arsil");
    request.setEmail("arsil@gmail.com");
    request.setUsername("21916060");
    request.setPassword("rahasia");
    request.setJurusanId("J0001");
    request.setGender(Gender.MALE.name());

    mvc.perform(
            post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      Jurusan findJurusan = jurusanRepository.findById("J0001").orElse(null);
      User findUser = userRepository.findById("21916060").orElse(null);
      assertNotNull(findJurusan);
      assertNotNull(findUser);

      assertEquals(response.getData(), "OK");
      assertEquals("Arsil", findUser.getName());
      assertEquals("arsil@gmail.com", findUser.getEmail());
      assertEquals("21916060", findUser.getUsername());
      assertTrue(BCrypt.checkpw(request.getPassword(), findUser.getPassword()));
      assertEquals("J0001", findUser.getJurusan().getId());
      assertEquals(Role.MAHASISWA, findUser.getRole());
      assertEquals(Gender.MALE, findUser.getGender());

    });
  }

  @Test
  void testRegisterFailed() throws Exception{
    RegisterUserRequest request = new RegisterUserRequest();
    request.setName("Arsil");
    request.setEmail("arsil@gmail.com");
    request.setUsername("");
    request.setPassword("rahasia");
    request.setJurusanId("J0001");

    mvc.perform(
            post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getError());
    });
  }

  @Test
  void testGetCurrentUserSuccess() throws Exception{
    Jurusan jurusan = new Jurusan();
    jurusan.setName("Pendidikan Teknologi Infromasi");
    jurusan.setId("J001");
    jurusanRepository.save(jurusan);

    User user = new User();
    user.setName("Arsil");
    user.setEmail("arsil@gmail.com");
    user.setUsername("008");
    user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
    user.setRole(Role.MAHASISWA);
    user.setGender(Gender.MALE);
    user.setJurusan(jurusan);
    user.setToken("token ku");
    user.setTokenExpiredAt(1000000L);
    userRepository.save(user);


    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "token ku")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData());
      assertEquals(user.getName(), response.getData().getName());
      assertEquals(user.getUsername(), response.getData().getUsername());
      assertEquals(user.getEmail(), response.getData().getEmail());


    });
  }

  @Test
  void testGetCurrentUserUnauthorized() throws Exception{


    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "salah")
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

  @Test
  void testUpdateCurrentUserSuccess() throws Exception{

    User user = new User();
    user.setName("Arsil");
    user.setEmail("arsil@gmail.com");
    user.setUsername("008");
    user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
    user.setRole(Role.MAHASISWA);
    user.setGender(Gender.FEMALE);
    user.setToken("token ku");
    user.setTokenExpiredAt(1000000L);
    userRepository.save(user);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("Nama baru");
    request.setAvatar("new-image.png");
    request.setPassword("new-rahasia");
    request.setGender("MALE");

    mvc.perform(
            patch("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "token ku")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData());
      User findUser = userRepository.findById("008").orElse(null);
      assertNotNull(findUser);

      assertEquals("Nama baru", findUser.getName());
      assertEquals("arsil@gmail.com", findUser.getEmail());
      assertEquals("008", findUser.getUsername());
      assertEquals("new-image.png", findUser.getAvatar());
      assertTrue(BCrypt.checkpw(request.getPassword(), findUser.getPassword()));
      assertNull(findUser.getJurusan());
      assertEquals(Gender.MALE, findUser.getGender());
      assertEquals(Role.MAHASISWA, findUser.getRole());


    });
  }

  @Test
  void testUpdateCurrentUserUnauthorized() throws Exception{
    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("Nama baru");
    request.setAvatar("new-image.png");
    request.setPassword("new-rahasia");

    mvc.perform(
            patch("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "salah")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
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

  @Test
  void testSearchNotFound() throws Exception{

    User user = new User();
    user.setName("Arsil");
    user.setEmail("arsil@gmail.com");
    user.setUsername("008");
    user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
    user.setRole(Role.MAHASISWA);
    user.setGender(Gender.FEMALE);
    user.setToken("token ku");
    user.setTokenExpiredAt(1000000L);
    userRepository.save(user);

    for (int i = 0; i < 15; i++) {
      User person = new User();
      person.setName("ucok" + i);
      person.setEmail("ucok"+ i+"@gmail.com");
      person.setUsername("008" + i);
      person.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
      person.setRole(Role.MAHASISWA);
      person.setGender(Gender.FEMALE);
      userRepository.save(person);
    }

    mvc.perform(
            get("/api/v1/users")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData());
      assertEquals(10, response.getData().size());


    });
  }

  @Test
  void testSearchSuccess() throws Exception{
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
    userRepository.save(user);

    for (int i = 0; i < 5; i++) {
      User person = new User();
      person.setName("ucok" + i);
      person.setEmail("ucok"+ i+"@gmail.com");
      person.setUsername("008" + i);
      person.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
      person.setRole(Role.MAHASISWA);
      person.setGender(Gender.FEMALE);

      if(i % 2 == 0){
        person.setJurusan(j2);
      }else{
        person.setJurusan(j3);
      }

      userRepository.save(person);
    }

    for (int i = 0; i < 5; i++) {
      User person1 = new User();
      person1.setName("udin" + i);
      person1.setEmail("udin"+ i+"@gmail.com");
      person1.setUsername(i+ "008" + i);
      person1.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
      person1.setRole(Role.MAHASISWA);
      person1.setGender(Gender.FEMALE);

      if(i % 2 == 0){
        person1.setJurusan(j1);
      }else{
        person1.setJurusan(j2);
      }
      userRepository.save(person1);
    }

    mvc.perform(
            get("/api/v1/users")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("jurusan","PAUD")
                    .header("X-API-TOKEN", "token ku")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      for(UserResponse data : response.getData()){
        System.out.println(data.getName() + ": " + data.getJurusan());
      }
      assertNotNull(response.getData());
      assertEquals(5, response.getData().size());


    });
  }
}