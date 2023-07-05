package com.lppaik.repository;

import com.lppaik.entity.Jurusan;
import com.lppaik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JurusanRepository extends JpaRepository<Jurusan, String> {
}
