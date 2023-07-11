package com.lppaik.endtoend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.*;
import com.lppaik.model.request.UpdateActivityRequest;
import com.lppaik.model.response.ActivityResponse;
import com.lppaik.model.response.ErrorResponse;
import com.lppaik.model.response.UserActivityResponse;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.CreateActivityRequest;
import com.lppaik.repository.*;
import com.lppaik.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityControllerTest {

  private final ObjectMapper mapper;

  private final MockMvc mvc;

  private final JurusanRepository jurusanRepository;

  private final UserRepository userRepository;

  private final BTQDetailsRepository btqDetailsRepository;

  private final BTQControlBookRepository bookRepository;

  private final ActivityRepository activityRepository;

  @Autowired
  public ActivityControllerTest(ObjectMapper mapper, MockMvc mvc, JurusanRepository jurusanRepository, UserRepository userRepository, BTQDetailsRepository repository, BTQControlBookRepository bookRepository, ActivityRepository activityRepository) {
    this.mapper = mapper;
    this.mvc = mvc;
    this.jurusanRepository = jurusanRepository;
    this.userRepository = userRepository;
    this.btqDetailsRepository = repository;
    this.bookRepository = bookRepository;
    this.activityRepository = activityRepository;
  }

  @BeforeEach
  void setUp() {

    btqDetailsRepository.deleteAll();
    bookRepository.deleteAll();
    activityRepository.findAll() // this operation only support if @ManyToMany have property fetch = FetchType.EAGER
            .forEach(activity -> {
                      activity.getUsers().forEach(u -> u.getActivities().remove(activity));
                      userRepository.saveAll(activity.getUsers());

                      activityRepository.delete(activity);
            });
    userRepository.deleteAll();
    jurusanRepository.deleteAll();


    /*
     * First create jurusan, for user register
     * */
    Jurusan jurusan = new Jurusan();
    jurusan.setId("Jxxx1");
    jurusan.setName("TEKNIK");

    jurusanRepository.save(jurusan);

    Activity activity = new Activity();
    activity.setDescription("kegiatan ini tentang bla bla bla 2");
    activity.setTitle("judul kegiatan 2");
    activity.setImage("poster-kegiatan.png 2");
    activity.setTime("waktu siang 2");
    activity.setLink("link kegiatan 2");
    activity.setLocation("tempat kegiatan 2");
    activity.setId("a0002");

    activityRepository.save(activity);

    Activity a2 = new Activity();
    a2.setDescription("test ini tentang bla bla bla 2");
    a2.setTitle("test kegiatan 2");
    a2.setImage("test-kegiatan.png 2");
    a2.setTime("test siang 2");
    a2.setLink("test kegiatan 2");
    a2.setLocation("test kegiatan 2");
    a2.setId("atest1");

    activityRepository.save(a2);

    User mahasiswa = new User();
    mahasiswa.setUsername("mhs-id");
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

    User admin = new User();
    admin.setUsername("admin-id");
    admin.setJurusan(jurusan);
    admin.setName("Mimin");
    admin.setEmail("mimin@gmail.com");
    admin.setRole(Role.ADMIN);
    admin.setGender(Gender.MALE);
    admin.setPassword(BCrypt.hashpw("secret123", BCrypt.gensalt()));
    admin.setToken("admin-token");
    admin.setTokenExpiredAt(100000L);

    userRepository.save(admin);

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
  void testDeleteActivitySuccess() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();


    mvc.perform(
            delete("/api/v1/activity/" + activity.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
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
  void testDeleteActivityErrorBecauseRole() throws Exception{

    User mahasiswa = userRepository.findById("mhs-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    mvc.perform(
            delete("/api/v1/activity/" + activity.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
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
  void testDeleteActivityUnauthorized() throws Exception{

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    mvc.perform(
            delete("/api/v1/activity/" + activity.getId())
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
  void testDeleteActivityNotFound() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();

    String wrongActivityId = "w-act-id";

    mvc.perform(
            delete("/api/v1/activity/" + wrongActivityId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Activity not found!", response.getError());

    });
  }

  @Test
  void testUpdateActivitySuccess() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    UpdateActivityRequest request = new UpdateActivityRequest();

    request.setTitle("new title req");
    request.setDescription("new desc req");
    request.setLink("new-link-req.html");
    request.setTime("Thursday");
    request.setImage("new-banner.png");
    request.setLocation("Bat bat");

    mvc.perform(
            patch("/api/v1/activity/" + activity.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", admin.getToken())
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
  void testUpdateActivityErrorBecauseRole() throws Exception{

    User mahasiswa = userRepository.findById("mhs-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    UpdateActivityRequest request = new UpdateActivityRequest();

    request.setTitle("new title req");
    request.setDescription("new desc req");
    request.setLink("new-link-req.html");
    request.setTime("Thursday");
    request.setImage("new-banner.png");
    request.setLocation("Bat bat");

    mvc.perform(
            patch("/api/v1/activity/" + activity.getId())
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
  void testUpdateActivityUnauthorized() throws Exception{

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    UpdateActivityRequest request = new UpdateActivityRequest();

    request.setTitle("new title req");
    request.setDescription("new desc req");
    request.setLink("new-link-req.html");
    request.setTime("Thursday");
    request.setImage("new-banner.png");
    request.setLocation("Bat bat");

    mvc.perform(
            patch("/api/v1/activity/" + activity.getId())
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
  void testUpdateActivityNotFound() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();

    String wrongActivityId = "w-act-id";

    UpdateActivityRequest request = new UpdateActivityRequest();

    request.setTitle("new title req");
    request.setDescription("new desc req");
    request.setLink("new-link-req.html");
    request.setTime("Thursday");
    request.setImage("new-banner.png");
    request.setLocation("Bat bat");

    mvc.perform(
            patch("/api/v1/activity/" + wrongActivityId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", admin.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Activity not found!", response.getError());

    });
  }

  @Test
  void testAddActivityToMahasiswaSuccess() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();
    User mahasiswa = userRepository.findById("mhs-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();


    mvc.perform(
            post("/api/v1/activity/" + activity.getId() + "/for/" + mahasiswa.getUsername())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
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
  void testAddActivityToMahasiswaErrorBecauseRole() throws Exception{

    User dosen = userRepository.findById("dosen-id").orElseThrow();
    User mahasiswa = userRepository.findById("mhs-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();


    mvc.perform(
            post("/api/v1/activity/" + activity.getId() + "/for/" + mahasiswa.getUsername())
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
  void testAddActivityToMahasiswaUnauthorized() throws Exception{

    User mahasiswa = userRepository.findById("mhs-id").orElseThrow();

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    mvc.perform(
            post("/api/v1/activity/" + activity.getId() + "/for/" + mahasiswa.getUsername())
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
  void testAddActivityToMahasiswaMhsNotFound() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();

    String wrongMhsId = "w-mhs-id";

    Activity activity = activityRepository.findById("a0002").orElseThrow();

    mvc.perform(
            post("/api/v1/activity/" + activity.getId() + "/for/" + wrongMhsId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", admin.getToken())
    ).andExpectAll(
            status().isNotFound()
    ).andExpectAll(result -> {
      ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals("Mahasiswa with id " + wrongMhsId + " is not found!", response.getError());

    });
  }
  @Test
  void testGetListActivitySuccess() throws Exception{

    mvc.perform(
            get("/api/v1/activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("title", "kegiatan")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<List<ActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(2, response.getData().size());

    });
  }

  @Test
  void testGetListActivitySuccessNotFound() throws Exception{

    mvc.perform(
            get("/api/v1/activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("title", "not found")
    ).andExpectAll(
            status().isOk()
    ).andExpectAll(result -> {
      WebResponse<List<ActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertEquals(0, response.getData().size());

    });
  }
  @Test
  void testCreateActivitySuccess() throws Exception{

    User admin = userRepository.findById("admin-id").orElseThrow();

    CreateActivityRequest request = new CreateActivityRequest();
    request.setDescription("long desc ");
    request.setTitle("simple title");
    request.setImage("simple-img.png ");
    request.setTime("morning");
    request.setLink("link.html");
    request.setLocation("my location");


    mvc.perform(
            post("/api/v1/activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", admin.getToken())
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
  void testCreateActivityErrorBecauseRole() throws Exception{

    User mahasiswa = userRepository.findById("mhs-id").orElseThrow();

    CreateActivityRequest request = new CreateActivityRequest();
    request.setDescription("long desc ");
    request.setTitle("simple title");
    request.setImage("simple-img.png ");
    request.setTime("morning");
    request.setLink("link.html");
    request.setLocation("my location");

    mvc.perform(
            post("/api/v1/activity")
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
  void testCreateActivityUnauthorized() throws Exception{

    CreateActivityRequest request = new CreateActivityRequest();
    request.setDescription("long desc ");
    request.setTitle("simple title");
    request.setImage("simple-img.png ");
    request.setTime("morning");
    request.setLink("link.html");
    request.setLocation("my location");


    mvc.perform(
            post("/api/v1/activity")
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


}
