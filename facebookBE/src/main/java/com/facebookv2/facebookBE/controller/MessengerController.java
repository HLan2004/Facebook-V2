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

    @GetMapping("/{conversationId}")
    public String conversation(@PathVariable Long conversationId,
                               Model model,
                               Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        // thông tin user hiện tại
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("currentUsername", user.getFirstName() + " " + user.getLastName());

        // danh sách conversation (vẫn hiển thị bên sidebar)
        List<ConversationSummaryDTO> conversations = conversationService.getConversationSummaries(user);
        model.addAttribute("conversations", conversations);

        // load messages cho conversation này
        List<ChatMessage> messages = chatMessageService.findByConversationId(conversationId);
        model.addAttribute("messages", messages);

        model.addAttribute("conversationId", conversationId);

        return "user/messenger";
    }
}