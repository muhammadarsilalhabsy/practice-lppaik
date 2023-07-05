package com.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jurusan")
public class Jurusan {

  @Id
  private String id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "jurusan")
  private List<User> users;
}
