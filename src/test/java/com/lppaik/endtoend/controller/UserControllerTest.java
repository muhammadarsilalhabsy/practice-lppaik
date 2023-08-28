package com.lppaik.endtoend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.*;
import com.lppaik.model.response.*;
import com.lppaik.model.request.UpdateUserRequest;
import com.lppaik.repository.*;
import com.lppaik.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  private final JurusanRepository jurusanRepository;

  private final UserRepository userRepository;

  private final BTQControlBookRepository bookRepository;

  private final BTQDetailsRepository btqDetailsRepository;
  private final ActivityRepository activityRepository;

  @Autowired
  public UserControllerTest(ObjectMapper mapper, MockMvc mvc, JurusanRepository jurusanRepository, UserRepository userRepository, BTQControlBookRepository bookRepository, BTQDetailsRepository btqDetailsRepository, ActivityRepository activityRepository) {
    this.mapper = mapper;
    this.mvc = mvc;
    this.jurusanRepository = jurusanRepository;
    this.userRepository = userRepository;
    this.bookRepository = bookRepository;
    this.btqDetailsRepository = btqDetailsRepository;
    this.activityRepository = activityRepository;
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

    Activity a2 = new Activity();
    a2.setDescription("test ini tentang bla bla bla 2");
    a2.setTitle("test kegiatan 2");
    a2.setImage("test-kegiatan.png 2");
    a2.setTime(LocalTime.now());
    a2.setDate(LocalDate.now());
    a2.setColor("bg-sky-400");
    a2.setLink("test kegiatan 2");
    a2.setLocation("test kegiatan 2");
    a2.setId("atest1");

    activityRepository.save(a2);

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
    mahasiswa.getActivities().add(a2);

    userRepository.save(mahasiswa);

    BTQControlBook book = new BTQControlBook();
    book.setStatus(false);
    book.setId("test-id");

    bookRepository.save(book);

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
  }

  @Test
  void testGetDetailsCurrentActivityUserSuccess() throws Exception{

    User mhs = userRepository.findById("test-id").orElseThrow();

    mvc.perform(
            get("/api/v1/users/current/activity/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", mhs.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<List<UserActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(1, response.getData().size());

    });
  }
  @Test
  void testGetListBTQDetailsCurrentUserSuccess() throws Exception{

    User tutor = userRepository.findById("tutor-id").orElseThrow();
    User mahasiswa = userRepository.findById("test-id").orElseThrow();
    BTQControlBook book = bookRepository.findById(mahasiswa.getUsername()).orElseThrow();

    for (int i = 0; i < 5; i++) {
      BTQDetails details = new BTQDetails();
      details.setTutors(tutor);
      details.setActivity("IQRO hal 2" +i + " - 3" + i);
      details.setBook(book);
      details.setDay("JUMAT");
      btqDetailsRepository.save(details);
    }

    mvc.perform(
            get("/api/v1/users/current/btq/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", mahasiswa.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<List<BTQResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(5,response.getData().size());

    });
  }

  @Test
  void testGetListBTQDetailsCurrentUserUnauthorized() throws Exception{

    mvc.perform(
            get("/api/v1/users/current/btq/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Unauthorized",response.getError());

    });
  }

  @Test
  void testGetAllUsersOrSearchNotFoundSuccess() throws Exception{


    User dosen = userRepository.findById("dosen-id").orElseThrow();

    mvc.perform(
            get("/api/v1/users")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("identity","sembarang")
                    .header("X-API-TOKEN", dosen.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      // when search not match a single users, then return nothing which means length of arraylist is 0
      assertEquals(0, response.getData().size());


    });
  }
  @Test
  void testGetAllUsersOrSearchBySingleParamSuccess() throws Exception{

    // search users by identity (username or name) or by jurusan
    // this api available for (tutor, admin and dosen)
    User dosen = userRepository.findById("dosen-id").orElseThrow();

    mvc.perform(
            get("/api/v1/users")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("identity","Otong")
                    .header("X-API-TOKEN", dosen.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(1, response.getData().size());

      User userFromDB = userRepository.findById("test-id").orElseThrow();

      assertEquals(userFromDB.getName(), response.getData().get(0).getName());
      assertEquals(userFromDB.getEmail(), response.getData().get(0).getEmail());
      assertEquals(userFromDB.getAvatar(), response.getData().get(0).getAvatar());
      assertEquals(userFromDB.getRole().name(), response.getData().get(0).getRole());
      assertEquals(userFromDB.getUsername(), response.getData().get(0).getUsername());
      assertEquals(userFromDB.getGender().name(), response.getData().get(0).getGender());
      assertEquals(userFromDB.getJurusan().getName(), response.getData().get(0).getJurusan());
    });
  }

  @Test
  void testGetAllUsersOrSearchUnauthorized() throws Exception{

    User mahasiswa = userRepository.findById("test-id").orElseThrow();

    mvc.perform(
            get("/api/v1/users")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("identity","Otong")
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
  void testGetCurrentUserSuccess() throws Exception{

    User user = userRepository.findById("test-id").orElseThrow();

    // just sent a header with key "X-API-TOKEN" and value "your-token" when you do login
    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<UserResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });


      assertEquals(user.getName(), response.getData().getName());
      assertEquals(user.getEmail(), response.getData().getEmail());
      assertEquals(user.getAvatar(), response.getData().getAvatar());
      assertEquals(user.getRole().name(), response.getData().getRole());
      assertEquals(user.getUsername(), response.getData().getUsername());
      assertEquals(user.getGender().name(), response.getData().getGender());
      assertEquals(user.getJurusan().getName(), response.getData().getJurusan());
    });
  }

  @Test
  void testGetCurrentUserUnauthorized() throws Exception{

    // when users didn't sent a header with key "X-API-TOKEN" and they're token
    // response should be Unauthorized
    mvc.perform(
            get("/api/v1/users/current")
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
  void testUpdateCurrentUserSuccess() throws Exception{

    User user = userRepository.findById("test-id").orElseThrow();

    UpdateUserRequest request = new UpdateUserRequest();
    request.setPassword("new pass");
    request.setAvatar("new-image.png");
    request.setEmail("new@gmail.com");
    request.setGender("FEMALE");
    request.setName("new otong");

    mvc.perform(
            patch("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<UserResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      User userFromDB = userRepository.findById("test-id").orElseThrow();

      assertEquals(request.getName(), response.getData().getName());
      assertEquals(request.getEmail(), response.getData().getEmail());
      assertEquals(request.getAvatar(), response.getData().getAvatar());
      assertEquals(Gender.FEMALE.name(), response.getData().getGender());
      assertTrue(BCrypt.checkpw(request.getPassword(), userFromDB.getPassword()));
    });
  }

  @Test
  void testUpdateCurrentUserUnauthorized() throws Exception{

    UpdateUserRequest request = new UpdateUserRequest();
    request.setPassword("new pass");
    request.setAvatar("new-image.png");
    request.setEmail("new@gmail.com");
    request.setGender("FEMALE");
    request.setName("new otong");

    mvc.perform(
            patch("/api/v1/users/current")
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
