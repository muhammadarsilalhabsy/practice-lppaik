package com.lppaik.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  @OneToOne(fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn(name="username", referencedColumnName = "id")
  private BTQControlBook btqBook;

  @Column(unique = true)
  private String token;

  @Column(name = "token_expired_at")
  private Long tokenExpiredAt;

  @OneToMany(mappedBy = "tutors")
  private List<BTQDetails> details;

//  @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.DETACH})
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "kegiatan_users",
          joinColumns = @JoinColumn(name = "users_username", referencedColumnName = "username"),
          inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
  private Set<Activity> activities = new HashSet<>();
}
