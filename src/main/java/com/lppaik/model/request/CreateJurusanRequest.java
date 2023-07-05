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
public class CreateJurusanRequest {

  @NotBlank
  @Size(max = 10)
  private String id;

  @NotBlank
  @Size(max = 100)
  private String name;

}
