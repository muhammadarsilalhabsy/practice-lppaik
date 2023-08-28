package com.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityResponse {

  private String id;
  private String title;
  private String image;
  private String color;
  private String location;
  private String description;
  private String link;
  private String time;
  private String date;
  private String day;

}
