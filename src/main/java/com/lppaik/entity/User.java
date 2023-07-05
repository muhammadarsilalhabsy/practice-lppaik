package com.lppaik.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  private String username;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String email;

  private String avatar;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Enumerated(value = EnumType.STRING)
  private Gender gender;

  @ManyToOne
  @JoinColumn(name = "jurusan_id", referencedColumnName = "id")
  private Jurusan jurusan;

  @OneToOne
  @PrimaryKeyJoinColumn(name="username", referencedColumnName = "id")
  private BTQControlBook book;

  @Column(unique = true)
  private String token;

  @Column(name = "token_expired_at")
  private Long tokenExpiredAt;
}
