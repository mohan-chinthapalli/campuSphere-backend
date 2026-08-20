package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.AiConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiConversationRepository extends JpaRepository<AiConversation, Long> {
    Optional<AiConversation> findByConversationKeyAndUserId(String conversationKey, Long userId);
    Optional<AiConversation> findByConversationKey(String conversationKey);
    Page<AiConversation> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);
}
