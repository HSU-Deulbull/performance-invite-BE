package com.deulbull.performance.domain.admin.repository;

import com.deulbull.performance.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Band를 JOIN FETCH로 함께 조회 (N+1 쿼리 방지)
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.band WHERE a.password = :password")
    Optional<Admin> findByPasswordWithBand(@Param("password") String password);
}
