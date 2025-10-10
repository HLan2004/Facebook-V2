package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.Comment;
import com.facebookv2.facebookBE.model.Reaction;
import com.facebookv2.facebookBE.model.Status;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.repository.CommentRepository;
import com.facebookv2.facebookBE.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentAndReactionService {
    @Autowired
    ReactionRepository reactionRepository;
    @Autowired
    private CommentRepository commentRepository;

    public void saveReaction(User currentUser ,Status status) {
        Reaction reaction = new Reaction();
        reaction.setUser(currentUser);
        reaction.setStatus(status);
        reactionRepository.save(reaction);
    }

    public void deleteReactionByUserAndStatus(User currentUser, Status status) {
        reactionRepository.deleteByUserIdAndStatusId(currentUser.getId(), status.getId());
    }

    public void saveComment(User currentUser, Status status, String content) {
        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setStatus(status);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    public Boolean existsByUserAndStatus(User currentUser, Status status) {
        return reactionRepository.existsByUserAndStatus(currentUser, status);
    }

    public Long countReactionByStatus( Status status) {
         return reactionRepository.countReactionByStatus(status);
    }

    public Long countCommentByStatus( Status status) {
        return commentRepository.countCommentByStatus(status);
    }
}

