package com.lppaik.repository;

import com.lppaik.entity.BTQDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BTQDetailsRepository extends JpaRepository<BTQDetails, String> {
}
