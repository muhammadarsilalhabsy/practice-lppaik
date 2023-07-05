package com.lppaik.model.request;

import jakarta.validation.constraints.Email;
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
public class RegisterUserRequest {

  @NotBlank
  @Size(max = 10)
  private String username;

  @NotBlank
  @Size(max = 200)
  private String password;

  @NotBlank
  @Size(max = 200)
  private String name;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String jurusanId;

  @NotBlank
  private String gender;


}
