package com.lppaik.controller;

import com.lppaik.entity.User;
import com.lppaik.model.WebResponse;
import com.lppaik.model.request.CreateBTQDetailsRequest;
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

  @PostMapping(path = "/{bookId}/details",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<BTQResponse> create(User user,
                                         @RequestBody CreateBTQDetailsRequest request,
                                         @PathVariable("bookId") String bookId){

    request.setBookId(bookId);

    BTQResponse response = service.create(user, request);

    return WebResponse.<BTQResponse>builder()
            .data(response)
            .build();
  }

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



}
