package com.deulbull.performance.domain.band.repository;

import com.deulbull.performance.domain.band.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
