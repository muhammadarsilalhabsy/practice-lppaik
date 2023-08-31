package com.lppaik.controller;

import com.lppaik.service.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/image")
public class ImageController {

  private final ImageServiceImpl service;

  @Autowired
  public ImageController(ImageServiceImpl service) {
    this.service = service;
  }


  @GetMapping(path = "/{name}", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<?> get(@PathVariable("name") String name){
    return ResponseEntity.status(HttpStatus.OK).body(service.getImageFromDb(name));
  }
}
