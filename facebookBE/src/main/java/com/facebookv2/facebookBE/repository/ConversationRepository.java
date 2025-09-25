package com.facebookv2.facebookBE.repository;

import com.facebookv2.facebookBE.model.Conversation;
import com.facebookv2.facebookBE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByParticipantsContaining(User user);
}

