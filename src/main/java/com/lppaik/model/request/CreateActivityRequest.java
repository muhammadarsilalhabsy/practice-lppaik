package com.lppaik.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateActivityRequest {

  @NotBlank
  @Size(max = 200)
  private String title;

  private MultipartFile image;

  @Size(max = 200)
  private String location;
  private String description;
  private String link;
  private String time;
  private String date;
  private String color;

}
