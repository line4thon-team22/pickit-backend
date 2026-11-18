package com.pickit.backend.chat.repository;

import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findFirstByRoomOrderByMessageIdDesc(ChatRoom room);

    List<ChatMessage> findByRoomOrderByMessageIdDesc(ChatRoom room, Pageable pageable);

    List<ChatMessage> findByRoomAndMessageIdLessThanOrderByMessageIdDesc(ChatRoom room, Long messageId, Pageable pageable);
}
