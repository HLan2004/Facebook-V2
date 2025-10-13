package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.model.Conversation;
import com.facebookv2.facebookBE.model.Friendship;
import com.facebookv2.facebookBE.model.FriendshipStatus;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.FriendDTO;
import com.facebookv2.facebookBE.repository.ConversationRepository;
import com.facebookv2.facebookBE.repository.FriendshipRepository;
import com.facebookv2.facebookBE.repository.UserRepository;
import com.facebookv2.facebookBE.service.ConversationService;
import com.facebookv2.facebookBE.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private ConversationService conversationService;

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
        Map<Long, String> directionMap = new HashMap<>(); // "SENT" hoặc "RECEIVED"

        for (User u : users) {
            // nếu là bản thân mình thì sẽ bỏ qua
            if (u.getId().equals(currentUser.getId())) continue;
            // tìm bản ghi trong bảng friendship
            Optional<Friendship> friendshipOptional = friendshipRepository.findFriendship(currentUser.getId(), u.getId());
            // kiểm tra status nếu bản ghi tồn tại
            if (friendshipOptional.isPresent()) {
                Friendship friendship = friendshipOptional.get();
                statusMap.put(u.getId(), friendship.getStatus());

                //  Kiểm tra ai là người gửi
                if (friendship.getUser().getId().equals(currentUser.getId())) {
                    directionMap.put(u.getId(), "SENT"); // mình là người gửi
                } else {
                    directionMap.put(u.getId(), "RECEIVED"); // người kia là người gửi
                }
            } else {
                statusMap.put(u.getId(), null);
            }
        }
        model.addAttribute("directionMap", directionMap);
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

                //  Kiểm tra ai là người gửi
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
        // 1️⃣ Cập nhật trạng thái Friendship
        friendshipRepository.acceptFriendRequest(currentUserId, friendId);

        // 2️⃣ Tạo conversation nếu chưa tồn tại
        User currentUser = userRepository.findUserById(currentUserId);
        User friend = userRepository.findUserById(friendId);

        // kiểm tra xem đã có conversation chung chưa
        boolean exists = conversationRepository.findByParticipantsContaining(currentUser)
                .stream()
                .anyMatch(conv -> conv.getParticipants().contains(friend) && !conv.isGroup());

        if (!exists) {
            Conversation newConv = new Conversation();
            newConv.setGroup(false);
            newConv.setName(null); // null vì chat riêng
            newConv.getParticipants().add(currentUser);
            newConv.getParticipants().add(friend);

            conversationService.save(newConv);
        }
    }

    @Override
    public List<FriendDTO> getAccepted(Long currentUserId) {
        // 1. Lấy tất cả mối quan hệ bạn bè đã ACCEPTED từ repository
        //    (Điều này yêu cầu bạn phải thêm query `findUserFriendships` vào FriendshipRepository như hướng dẫn trước)
        List<Friendship> friendships = friendshipRepository.findUserFriendships(currentUserId, FriendshipStatus.ACCEPTED);

        // 2. Dùng stream để xử lý và chuyển đổi dữ liệu
        return friendships.stream()
                .map(friendship -> {
                    // 3. Với mỗi mối quan hệ, tìm ra ai là "người bạn" (không phải là người dùng hiện tại)
                    User friendUser = friendship.getUser().getId().equals(currentUserId)
                            ? friendship.getFriend()
                            : friendship.getUser();

                    // 4. Tạo một FriendDTO với thông tin cần thiết (ID, FullName, Avatar)
                    String fullName = friendUser.getFirstName() + " " + friendUser.getLastName();
                    return new FriendDTO(friendUser.getId(), fullName, friendUser.getAvatar());
                })
                .collect(Collectors.toList()); // 5. Thu thập kết quả vào một List
    }

    @Override
    public List<User> getAcceptedFriends(Long userId) {
        List<User> friends1 = friendshipRepository.findAcceptedFriendsAsUser(userId);
        List<User> friends2 = friendshipRepository.findAcceptedFriendsAsFriend(userId);

        friends1.addAll(friends2);
        return friends1;
    }


}
