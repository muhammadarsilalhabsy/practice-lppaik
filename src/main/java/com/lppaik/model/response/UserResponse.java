package com.lppaik.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

  private String username;

  private String name;

  private String avatar;

  private String email;

  private String jurusan;

  private String role;

  private String gender;
}
