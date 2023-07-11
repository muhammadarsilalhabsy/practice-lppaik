package com.lppaik.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lppaik.entity.*;
import com.lppaik.model.response.ErrorResponse;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.CreateActivityRequest;
import com.lppaik.model.request.UpdateActivityRequest;
import com.lppaik.model.response.ActivityResponse;
import com.lppaik.model.response.UserResponse;
import com.lppaik.repository.ActivityRepository;
import com.lppaik.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerTest {

  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;
  private final ObjectMapper mapper;
  private final MockMvc mvc;

  @Autowired
  public ActivityControllerTest(ActivityRepository activityRepository, UserRepository userRepository, ObjectMapper mapper, MockMvc mvc) {
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
    this.mapper = mapper;
    this.mvc = mvc;
  }

  @BeforeEach
  void setUp() {
    activityRepository.deleteAll();
    userRepository.deleteAll();

    User admin = new User();
    admin.setRole(Role.ADMIN);
    admin.setEmail("admin@gmail.com");
    admin.setToken("admin-token");
    admin.setUsername("admin123");
    admin.setPassword("rahasia-admin");
    admin.setName("admin btq");

    userRepository.save(admin);

    User tutor = new User();
    tutor.setRole(Role.TUTOR);
    tutor.setEmail("tutor@gmail.com");
    tutor.setToken("tutor-token");
    tutor.setUsername("tutor123");
    tutor.setPassword("rahasia-tutor");
    tutor.setName("tutor btq");

    userRepository.save(tutor);

    User mahasiswa = new User();
    mahasiswa.setRole(Role.MAHASISWA);
    mahasiswa.setEmail("mahasiswa@gmail.com");
    mahasiswa.setToken("mahasiswa-token");
    mahasiswa.setUsername("mhs123");
    mahasiswa.setPassword("rahasia-mahasiswa");
    mahasiswa.setName("mahasiswa btq");

    userRepository.save(mahasiswa);

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
  }

  @Test
  void testSearchCurrentActivityUser() throws Exception{

    User user = userRepository.findById("mhs123").orElse(null);
    Activity activity = activityRepository.findById("a0002").orElse(null);

    Activity activity2 = new Activity();
    activity2.setDescription(" bla 2");
    activity2.setTitle("kegiatan 2");
    activity2.setImage("-kegiatan.png 2");
    activity2.setTime(" siang 2");
    activity2.setLink(" kegiatan 2");
    activity2.setLocation(" kegiatan 2");
    activity2.setId("a0x02");

    activityRepository.save(activity2);
    user.getActivities().add(activity);
    user.getActivities().add(activity2);
    userRepository.save(user);

    mvc.perform(
            get("/api/v1/users/current/activity/details")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "mahasiswa-token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData());
      assertEquals(2, response.getData().size());


    });
  }
  @Test
  void testSearchAll() throws Exception{

    mvc.perform(
            get("/api/v1/activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<UserResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNotNull(response.getData());
      assertEquals(1, response.getData().size());


    });
  }

  @Test
  void testSearchTitleSuccess() throws Exception{

    for (int i = 0; i < 10; i++) {

      Activity activity = new Activity();
      activity.setDescription("kegiatan " + i);
      activity.setTitle("minum " + i);
      activity.setImage("poster.png" + i);
      activity.setTime("waktu " + i);
      activity.setLink("link" + i);
      activity.setLocation("tempat kegiatan" + i);
      activity.setId("a0002" + i);

      activityRepository.save(activity);
    }

    for (int i = 0; i < 10; i++) {

      Activity activity = new Activity();
      activity.setDescription("baru baru " + i);
      activity.setTitle("judul menyelam " + i);
      activity.setImage("baru baru.png" + i);
      activity.setTime("baru baru " + i);
      activity.setLink("baru baru" + i);
      activity.setLocation("baru kegiatan" + i);
      activity.setId("a0001" + i);

      activityRepository.save(activity);
    }

    mvc.perform(
            get("/api/v1/activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("title","judul")
                    .header("X-API-TOKEN", "token ku")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<ActivityResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      for(ActivityResponse data : response.getData()){
        System.out.println(data.getTitle() + ": " + data.getId());
      }
      assertNotNull(response.getData());
      assertEquals(10, response.getData().size());
      assertEquals(2, response.getPaging().getTotalPage());


    });
  }
  @Test
  void updateSuccess() throws Exception{

    UpdateActivityRequest request = new UpdateActivityRequest();
    request.setDescription("desc baru");
    request.setTitle("title baru");
    request.setLink("link baru");
    request.setLocation("lokasi baru");
    request.setImage("img-baru.png");
    request.setTime("waktu baru");

    mvc.perform(
              patch("/api/v1/activity/a0002")
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
        assertEquals("OK", response.getData());
        Activity activityFromDb = activityRepository.findById("a0002").orElse(null);
        assertNotNull(activityFromDb);

        assertEquals(request.getTime(), activityFromDb.getTime());
        assertEquals(request.getLink(), activityFromDb.getLink());
        assertEquals(request.getImage(), activityFromDb.getImage());
        assertEquals(request.getTitle(), activityFromDb.getTitle());
        assertEquals(request.getLocation(), activityFromDb.getLocation());
        assertEquals(request.getDescription(), activityFromDb.getDescription());
      });
  }

  @Test
  void updateFailedBecauseRole() throws Exception{

    UpdateActivityRequest request = new UpdateActivityRequest();
    request.setDescription("desc baru");
    request.setTitle("title baru");
    request.setLink("link baru");
    request.setLocation("lokasi baru");
    request.setImage("img-baru.png");
    request.setTime("waktu baru");

    mvc.perform(
        patch("/api/v1/activity/a0002")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .header("X-API-TOKEN", "tutor-token"))
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
  void updateUnauthorized() throws Exception{

    mvc.perform(
      patch("/api/v1/activity/a0002")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON))
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
  void updateActivityNotFound() throws Exception{

    UpdateActivityRequest request = new UpdateActivityRequest();
    request.setDescription("desc baru");
    request.setTitle("title baru");
    request.setLink("link baru");
    request.setLocation("lokasi baru");
    request.setImage("img-baru.png");
    request.setTime("waktu baru");

    mvc.perform(
            patch("/api/v1/activity/a0002x")
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
        assertEquals("Activity not found!", response.getError());

      });
  }

  @Test
  void deleteSuccess() throws Exception{

    User tutor = userRepository.findById("tutor123").orElse(null);
    User admin = userRepository.findById("admin123").orElse(null);

    Activity activity = activityRepository.findById("a0002").orElse(null);
    Activity a2 = activityRepository.findById("atest1").orElse(null);

    activity.setUsers(new HashSet<>());
    activity.getUsers().add(tutor);
    activity.getUsers().add(admin);

    a2.setUsers(new HashSet<>());
    a2.getUsers().add(admin);

    mvc.perform(
              delete("/api/v1/activity/a0002")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "admin-token"))
      .andExpectAll(
              status().isOk())
      .andDo(result -> {
        WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals("OK", response.getData());

      });
  }

  @Test
  void deleteFailedBecauseRole() throws Exception{

    mvc.perform(
              delete("/api/v1/activity/a0002")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "tutor-token"))
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
  void deleteUnauthorized() throws Exception{

    mvc.perform(
              delete("/api/v1/activity/a0002")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON))
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
  void deleteActivityNotFound() throws Exception{

    mvc.perform(
              delete("/api/v1/activity/a0002x")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "admin-token"))
      .andExpectAll(
              status().isNotFound())
      .andDo(result -> {
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals("Activity not found!", response.getError());

      });
  }

  @Test
  void createSuccess() throws Exception{

    CreateActivityRequest request = new CreateActivityRequest();
    request.setDescription("kegiatan ini tentang bla bla bla");
    request.setTitle("judul kegiatan");
    request.setImage("poster-kegiatan.png");
    request.setTime("waktu siang");
    request.setLink("link kegiatan");
    request.setLocation("tempat kegiatan");

    mvc.perform(
              post("/api/v1/activity")
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
        assertEquals("OK", response.getData());

        Activity fromDb = activityRepository.findById("a0001").orElse(null);
        assertNotNull(fromDb);

        assertEquals(request.getTime(), fromDb.getTime());
        assertEquals(request.getLink(), fromDb.getLink());
        assertEquals(request.getImage(), fromDb.getImage());
        assertEquals(request.getTitle(), fromDb.getTitle());
        assertEquals(request.getLocation(), fromDb.getLocation());
        assertEquals(request.getDescription(), fromDb.getDescription());


      });
  }

  @Test
  void createFailedBecauseRole() throws Exception{

    CreateActivityRequest request = new CreateActivityRequest();
    request.setDescription("kegiatan ini tentang bla bla bla");
    request.setTitle("judul kegiatan");
    request.setImage("poster-kegiatan.png");
    request.setTime("waktu siang");
    request.setLink("link kegiatan");
    request.setLocation("tempat kegiatan");

    mvc.perform(
            post("/api/v1/activity")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "tutor-token"))
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
  void createUnauthorized() throws Exception{

    CreateActivityRequest request = new CreateActivityRequest();
    request.setDescription("kegiatan ini tentang bla bla bla");
    request.setTitle("judul kegiatan");
    request.setImage("poster-kegiatan.png");
    request.setTime("waktu siang");
    request.setLink("link kegiatan");
    request.setLocation("tempat kegiatan");

    mvc.perform(
              post("/api/v1/activity")
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
  void assignToMahasiswaSuccess() throws Exception{

    mvc.perform(
              post("/api/v1/activity/a0002/for/mhs123")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "admin-token"))
      .andExpectAll(
              status().isOk())
      .andDo(result -> {
        WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals("OK", response.getData());

        Activity fromDb = activityRepository.findById("a0002").orElse(null);
        assertNotNull(fromDb);

        User userFromDb = userRepository.findById("mhs123").orElse(null);
        assertNotNull(userFromDb);

        assertEquals(1, userFromDb.getActivities().size());

      });
  }

  @Test
  void assignToMahasiswaActivityNotFound() throws Exception{

    mvc.perform(
              post("/api/v1/activity/a0002x/for/mhs123")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "admin-token"))
      .andExpectAll(
              status().isNotFound())
      .andDo(result -> {
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals("Activity not found!", response.getError());


      });
  }

  @Test
  void assignToMahasiswaMahasiswaNotFound() throws Exception{

    mvc.perform(
              post("/api/v1/activity/a0002x/for/mhs123x")
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("X-API-TOKEN", "admin-token"))
      .andExpectAll(
              status().isNotFound())
      .andDo(result -> {
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals("User not found!", response.getError());


      });
  }

  @Test
  void assignToMahasiswaFailedBecauseRole() throws Exception{

    mvc.perform(
            post("/api/v1/activity/a0002/for/mhs123")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "tutor-token"))
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
  void assignToMahasiswaUnauthorized() throws Exception{

    mvc.perform(
                    post("/api/v1/activity/a0002/for/mhs123")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                    status().isUnauthorized())
            .andDo(result -> {
              ErrorResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                      new TypeReference<>() {
                      });
              assertEquals("Unauthorized", response.getError());


            });
  }
}