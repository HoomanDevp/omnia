package com.omnia.core.resilience.repository;

import com.omnia.core.resilience.entity.Error;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorRepository extends JpaRepository<Error, Long> {

    Optional<Error> findByErrorCode(String errorCode);

    Optional<Error> findByIdAndErrorCode(Long id, String errorCode);
}