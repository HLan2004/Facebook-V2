package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.Friendship;
import com.facebookv2.facebookBE.model.FriendshipStatus;
import com.facebookv2.facebookBE.model.User;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

public interface FriendshipService {
    void addFriendship(String currentEmail,Long friendId);

    void checkFriendship(User currentUser, List<User> users, Model model);

    void checkFriendship1to1(User currentUser, User user, Model model);

    void deleteFriendshipRecord(Long currentUserId, Long friendId);

    void acceptFriendRequest(Long currentUserId, Long friendId);

    void declineFriendRequest(Long currentUserId, Long friendId);

    List<User> getAcceptedFriends(Long userId);
}
