package com.codecool.jpa.repository;

import com.codecool.jpa.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
}
