package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.response.WebResponse;
import com.lppaik.model.request.CreateBTQDetailsRequest;
import com.lppaik.model.request.UpdateBTQDetailsRequest;
import com.lppaik.model.response.BTQResponse;
import com.lppaik.service.BTQDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/btq")
public class BTQController {

  private final BTQDetailsServiceImpl service;

  @Autowired
  public BTQController(BTQDetailsServiceImpl service) {
    this.service = service;
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
  @DeleteMapping(path = "/tutor/{detailId}/details",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> delete(User user, @PathVariable("detailId") Long detailId){

    service.delete(user, detailId);

    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }

  // TUTOR
  @PatchMapping(path = "/tutor/{detailId}/details",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<BTQResponse> updateDetail(User user,
                                               @PathVariable("detailId") Long detailId,
                                               @RequestBody UpdateBTQDetailsRequest request){
    request.setDetailId(detailId);

    BTQResponse response = service.update(user, request);

    return WebResponse.<BTQResponse>builder().data(response).build();
  }


}
