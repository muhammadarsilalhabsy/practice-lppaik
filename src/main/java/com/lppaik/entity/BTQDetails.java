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
@Table(name = "btq_details")
public class BTQDetails {

  @Id
  private String id;
  private String day;
  private String activity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buku_id", referencedColumnName = "id")
  private BTQControlBook books;


}
