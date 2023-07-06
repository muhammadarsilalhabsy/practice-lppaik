package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.request.UpdateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.service.BTQDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/btq")
public class BTQController {

  private final BTQDetailsServiceImpl service;

  @Autowired
  public BTQController(BTQDetailsServiceImpl service) {
    this.service = service;
  }

  // MAHASISWA
  @GetMapping(path = "/{bookId}/details",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<BTQResponse>> getAll(User user,
                                               @PathVariable("bookId") String bookId){

    List<BTQResponse> responses = service.getDetails(user, bookId);

    return WebResponse.<List<BTQResponse>>builder()
            .data(responses)
            .build();
  }

  // TUTOR
  @PostMapping(path = "/tutor/{mahasiswaId}/details",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> createDetailBTQ(User user,
                                             @RequestBody CreateBTQDetailsRequest request,
                                             @PathVariable("mahasiswaId") String mahasiswaId){
    request.setBookId(mahasiswaId);

    service.createForMahasiswa(user, request, mahasiswaId);
    return WebResponse.<String>builder().data("OK").build();
  }

  // TUTOR
  @DeleteMapping(path = "/tutor/{detailId}/details")
  public WebResponse<String> delete(User user, @PathVariable("detailId") Long detailId){

    service.delete(user, detailId);

    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }

  // TUTOR
  @PatchMapping(path = "/tutor/{detailId}/details")
  public WebResponse<BTQResponse> updateDetail(User user,
                                             @RequestBody UpdateBTQDetailsRequest request,
                                             @PathVariable("detailId") Long detailId){
    request.setDetailId(detailId);

    BTQResponse response = service.update(user, request);

    return WebResponse.<BTQResponse>builder().data(response).build();
  }


}
