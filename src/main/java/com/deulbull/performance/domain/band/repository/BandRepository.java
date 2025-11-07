package com.deulbull.performance.domain.band.repository;

import com.deulbull.performance.domain.band.entity.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {
}
