package com.lppaik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UpdateActivityRequest {


  @JsonIgnore
  @NotBlank
  private String activityId;

  private String title;

  private String image;

  private String location;
  private String description;
  private String link;
  private String time;
}
