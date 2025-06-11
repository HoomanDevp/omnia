package com.omnia.core.message.repository;

import com.omnia.core.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByKey(String key);

    Optional<Message> findByIdAndKey(Long id, String key);
}