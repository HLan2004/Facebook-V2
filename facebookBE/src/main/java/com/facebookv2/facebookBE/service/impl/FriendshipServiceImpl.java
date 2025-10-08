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
    public void checkFriendship(User currentUser, List<User> users, Model model) {
        Map<Long, FriendshipStatus> statusMap = new HashMap<>();
        for (User u : users) {
            // nếu là bản thân mình thì sẽ bỏ qua
            if (u.getId().equals(currentUser.getId())) continue;
            // tìm bản ghi trong bảng friendship
            Optional<Friendship> friendshipOptional = friendshipRepository.findFriendship(currentUser.getId(), u.getId());
            // kiểm tra status nếu bản ghi tồn tại
            if (friendshipOptional.isPresent()) {
                Friendship friendship = friendshipOptional.get();
                statusMap.put(u.getId(), friendship.getStatus());
            } else {
                statusMap.put(u.getId(), null);
            }
        }

        model.addAttribute("statusMap", statusMap);
    }


    @Override
    public void checkFriendship1to1(User currentUser, User user, Model model) {
        Map<Long, FriendshipStatus> statusMap = new HashMap<>();
        Map<Long, String> directionMap = new HashMap<>(); // "SENT" hoặc "RECEIVED"

        Long targetUserId = user.getId();

        // Nếu là chính mình → không có trạng thái hoặc hướng
        if (currentUser.getId().equals(targetUserId)) {
            statusMap.put(targetUserId, null);
            directionMap.put(targetUserId, null);
        } else {
            // Tìm quan hệ bạn bè giữa 2 người
            Optional<Friendship> friendshipOpt =
                    friendshipRepository.findFriendship(currentUser.getId(), targetUserId);

            if (friendshipOpt.isPresent()) {
                Friendship friendship = friendshipOpt.get();
                statusMap.put(targetUserId, friendship.getStatus());

                // ✅ Kiểm tra ai là người gửi
                if (friendship.getUser().getId().equals(currentUser.getId())) {
                    directionMap.put(targetUserId, "SENT"); // mình là người gửi
                } else {
                    directionMap.put(targetUserId, "RECEIVED"); // người kia là người gửi
                }

            } else {
                // Không có mối quan hệ
                statusMap.put(targetUserId, null);
                directionMap.put(targetUserId, null);
            }
        }
        model.addAttribute("statusMap", statusMap);
        model.addAttribute("directionMap", directionMap);
    }


    @Override
    public void deleteFriendshipRecord(Long currentUserId, Long friendId) {
        friendshipRepository.deleteFriendshipRecord(currentUserId, friendId);
    }

    @Override
    public void acceptFriendRequest(Long currentUserId, Long friendId) {
        friendshipRepository.acceptFriendRequest(currentUserId, friendId);
    }

    @Override
    public void declineFriendRequest(Long currentUserId, Long friendId) {
        friendshipRepository.declineFriendRequest(currentUserId, friendId);
    }


}
