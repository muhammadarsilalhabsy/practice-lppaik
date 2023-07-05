package com.lppaik.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "buku_c_btq")
public class BTQControlBook {

  @Id
  private String id;

  private Boolean status;

  @OneToOne(mappedBy = "btqBook")
  private User user;

  @OneToMany(mappedBy = "book")
  private List<BTQDetails> details;

}
