package com.lppaik.repository;

import com.lppaik.entity.BTQControlBook;
import com.lppaik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BTQControlBookRepository extends JpaRepository<BTQControlBook, String> {
  Optional<BTQControlBook> findFirstByUserAndId(User user, String bookId);

}
