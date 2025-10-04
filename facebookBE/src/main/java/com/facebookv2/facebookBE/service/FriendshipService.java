package com.facebookv2.facebookBE.service;

import com.facebookv2.facebookBE.model.Friendship;
import org.springframework.ui.Model;

public interface FriendshipService {
    void addFriendship(String currentEmail,Long friendId);

    void checkFriendship(String keyword, String currentUserEmail, Model model);
}
