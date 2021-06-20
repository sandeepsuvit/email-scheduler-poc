package com.needle.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.needle.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

}
