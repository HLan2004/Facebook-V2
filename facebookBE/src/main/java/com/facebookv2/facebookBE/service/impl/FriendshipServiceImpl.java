package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.Friendship;
import com.facebookv2.facebookBE.model.FriendshipStatus;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.repository.FriendshipRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.FriendshipService;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public void addFriendship(@ModelAttribute String currentEmail, Long friendId) {
        User currentUser = userRepository.findByEmail(currentEmail);        // current user id
        User friend = userRepository.findUserById(friendId);

        Friendship friendship = new Friendship();
        friendship.setUser(currentUser);
        friendship.setFriend(friend);

        friendshipRepository.save(friendship);

    }

    @Override
    public void checkFriendship(String keyword, String currentUserEmail, Model model) {
//        Model model =
        User currentUser = userService.getUserByEmail(currentUserEmail);
        List<User> users = userService.searchByName(keyword);
        // xử lý nút kết bạn
        Map<Long, FriendshipStatus> statusMap = new HashMap<>();
        for (User u : users) {
            if (u.getId().equals(currentUser.getId())) {
                continue; // bỏ qua chính mình
            }
            Optional<Friendship> friendshipOptional = friendshipRepository.findFriendship(currentUser.getId(), u.getId());
            if (friendshipOptional.isPresent()) {
                statusMap.put(u.getId(), friendshipOptional.get().getStatus());
            } else {
                statusMap.put(u.getId(), null);
            }
        }
        model.addAttribute("statusMap", statusMap);
        model.addAttribute("users", users);
    }


}
