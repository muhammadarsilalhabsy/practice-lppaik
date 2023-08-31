package com.lppaik.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

  @Size(max = 200)
  private String password;

  @Size(max = 200)
  private String name;

  @Email
  private String email;

  private String gender;

}
