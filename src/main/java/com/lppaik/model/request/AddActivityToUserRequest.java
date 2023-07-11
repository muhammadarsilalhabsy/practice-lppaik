package com.lppaik.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddActivityToUserRequest {

  @NotBlank
  private String activityId;

  @NotBlank
  private String toUserId;
}
