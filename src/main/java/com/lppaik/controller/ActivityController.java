package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.*;
import com.lppaik.model.response.ActivityResponse;
import com.lppaik.model.response.PagingResponse;
import com.lppaik.service.ActivityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/activity")
public class ActivityController {

  private final ActivityServiceImpl service;

  @Autowired
  public ActivityController(ActivityServiceImpl service) {
    this.service = service;
  }

  // ALL ROLE
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<ActivityResponse>> searchActivity(@RequestParam(value = "title", required = false) String title,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){

    SearchActivityRequest request = SearchActivityRequest.builder()
            .title(title)
            .page(page)
            .size(size)
            .build();

    Page<ActivityResponse> response = service.searchActivity(request);
    return WebResponse.<List<ActivityResponse>>builder()
            .data(response.getContent())
            .paging(PagingResponse.builder()
                    .currentPage(response.getNumber())
                    .totalPage(response.getTotalPages())
                    .size(response.getSize())
                    .build())
            .build();
  }

  // ADMIN ONLY
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> create(User user, @RequestBody CreateActivityRequest request){

    service.create(user, request);
    return WebResponse.<String>builder().data("OK").build();
  }

  // ADMIN and KETUA
  @PostMapping(path = "/{activityId}/for/{mahasiswaId}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> addActivityToMahasiswa(User user,
                                                    @PathVariable("activityId") String activityId,
                                                    @PathVariable("mahasiswaId") String mahasiswaId,
                                                    AddActivityToUserRequest request){

    request.setActivityId(activityId);
    request.setToUserId(mahasiswaId);

    service.addActivityToMahasiswa(user, request);
    return WebResponse.<String>builder().data("OK").build();
  }

  // ADMIN ONLY
  @PatchMapping(path = "/{activityId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> updateActivity(User user,
                                            @PathVariable("activityId") String activityId,
                                            @RequestBody UpdateActivityRequest request){

    request.setActivityId(activityId);
    service.update(user, request);

    return WebResponse.<String>builder().data("OK").build();
  }

  // ADMIN ONLY
  @DeleteMapping(path = "/{activityId}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> deleteActivity(User user,
                                            @PathVariable("activityId") String activityId){


    service.delete(user, activityId);

    return WebResponse.<String>builder().data("OK").build();
  }
}
