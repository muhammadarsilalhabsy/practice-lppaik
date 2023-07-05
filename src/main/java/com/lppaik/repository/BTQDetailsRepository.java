package com.lppaik.repository;

import com.lppaik.entity.BTQControlBook;
import com.lppaik.entity.BTQDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BTQDetailsRepository extends JpaRepository<BTQDetails, Long> {
  List<BTQDetails> findAllByBook(BTQControlBook book);
}
