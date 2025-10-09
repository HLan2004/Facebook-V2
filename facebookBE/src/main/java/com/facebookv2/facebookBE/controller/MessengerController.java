package com.facebookv2.facebookBE.controller;

import com.facebookv2.facebookBE.model.ChatMessage;
import com.facebookv2.facebookBE.model.User;
import com.facebookv2.facebookBE.model.dto.ConversationSummaryDTO;
import com.facebookv2.facebookBE.service.ChatMessageService;
import com.facebookv2.facebookBE.service.ConversationService;
import com.facebookv2.facebookBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/facebook/messenger")
public class MessengerController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ChatMessageService chatMessageService;

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
}
