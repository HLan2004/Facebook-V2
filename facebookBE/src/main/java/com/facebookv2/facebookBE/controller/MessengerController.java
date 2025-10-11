package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.Conversation;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;
import com.facebookv2.facebookBE.model.dto.CreateGroupChatRequest;
import com.facebookv2.facebookBE.model.dto.UserSummaryDTO;
import com.facebookv2.facebookBE.service.ChatMessageService;
import com.facebookv2.facebookBE.service.ConversationService;
import com.facebookv2.facebookBE.service.FriendshipService;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/facebook/messenger")
public class MessengerController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private FriendshipService friendshipService;

    // Trang Messenger chính — chỉ load layout
    @GetMapping
    public String messengerPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("currentUsername", user.getFirstName() + " " + user.getLastName());
        return "user/messenger";
    }

    // API lấy danh sách cuộc trò chuyện
    @GetMapping("/api/conversations")
    @ResponseBody
    public List<ConversationSummaryDTO> getConversations(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        return conversationService.getConversationSummaries(user);
    }

    // API lấy tin nhắn 1 conversation
    @GetMapping("/api/{conversationId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable Long conversationId) {
        return chatMessageService.findByConversationId(conversationId);
    }


    // Thêm endpoint này vào MessengerController.java


    @PostMapping("/api/conversations/group")
    @ResponseBody
    public ResponseEntity<?> createGroupConversation(@RequestBody CreateGroupChatRequest request,
                                                     Authentication authentication) {
        try {
            String email = authentication.getName();
            User currentUser = userService.getUserByEmail(email);

            // Kiểm tra tên group không trống
            if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên nhóm không được để trống");
            }

            // Kiểm tra danh sách thành viên
            if (request.getParticipantIds() == null || request.getParticipantIds().size() < 1) {
                return ResponseEntity.badRequest().body("Cần ít nhất 1 thành viên khác để tạo nhóm");
            }

            // Tạo conversation mới
            Conversation groupConversation = new Conversation();
            groupConversation.setGroup(true);
            groupConversation.setName(request.getGroupName().trim());

            // Thêm người tạo nhóm
            groupConversation.getParticipants().add(currentUser);

            // Thêm các thành viên được chọn
            for (Long participantId : request.getParticipantIds()) {
                User participant = userService.findById(participantId); // Sử dụng findById thay vì getUserById
                if (participant != null) {
                    groupConversation.getParticipants().add(participant);
                }
            }

            Conversation savedConversation = conversationService.save(groupConversation);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "conversationId", savedConversation.getId(),
                    "message", "Tạo nhóm thành công"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo nhóm");
        }
    }

    // Thêm endpoint này vào MessengerController.java

    @GetMapping("/api/friends")
    @ResponseBody
    public ResponseEntity<List<UserSummaryDTO>> getFriends(Authentication authentication) {
        try {
            String email = authentication.getName();
            User currentUser = userService.getUserByEmail(email);
            System.out.println("Current user: " + currentUser);

            List<User> friends = friendshipService.getAcceptedFriends(currentUser.getId());
            System.out.println("Friends: " + friends);

            List<UserSummaryDTO> friendSummaries = friends.stream()
                    .map(friend -> new UserSummaryDTO(
                            friend.getId(),
                            friend.getFirstName() + " " + friend.getLastName(),
                            friend.getAvatar() != null ? friend.getAvatar() : "/images/default-avatar.png"
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(friendSummaries);
        } catch (Exception e) {
            e.printStackTrace(); // in ra lỗi để biết nguyên nhân
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
