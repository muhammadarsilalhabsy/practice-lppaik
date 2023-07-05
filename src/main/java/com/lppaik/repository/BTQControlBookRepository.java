package com.lppaik.repository;

import com.lppaik.entity.BTQControlBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BTQControlBookRepository extends JpaRepository<BTQControlBook, String> {
}
