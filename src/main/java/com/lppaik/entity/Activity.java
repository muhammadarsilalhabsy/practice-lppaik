package com.lppaik.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kegiatan")
public class Activity {

  @Id
  private String id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String image;

  private String location;

  private String description;

  private String link;

  private String time;

//  @ManyToMany(mappedBy = "activities", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
//  @ManyToMany(mappedBy = "activities", fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.DETACH})
  @ManyToMany(mappedBy = "activities", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();
}
