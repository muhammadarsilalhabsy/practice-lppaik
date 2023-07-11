package com.lppaik.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateActivityRequest {

  @NotBlank
  @Size(max = 200)
  private String title;

  @NotBlank
  private String image;

  @Size(max = 200)
  private String location;
  private String description;
  private String link;
  private String time;

}
