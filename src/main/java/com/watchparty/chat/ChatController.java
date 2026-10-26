package com.watchparty.chat;

import com.watchparty.auth.User;
import com.watchparty.auth.UserRepository;
import com.watchparty.room.Room;
import com.watchparty.room.RoomRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/rooms/{code}/chat")
public class ChatController {
    private final RoomRepository rooms;
    private final UserRepository users;
    private final ChatRepository chat;

    public ChatController(RoomRepository rooms, UserRepository users, ChatRepository chat) {
        this.rooms = rooms;
        this.users = users;
        this.chat = chat;
    }

    private Room room(String code, Authentication authentication) {
        Room room = rooms.findByCode(normalizeCode(code))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        User user = users.findByUsername(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!room.hasMember(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Join this room first");
        }
        return room;
    }

    @GetMapping
    public List<ChatMessage> messages(@PathVariable String code,
                                      @RequestParam(defaultValue = "0") long afterId,
                                      Authentication authentication) {
        if (afterId < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "afterId cannot be negative");
        }
        Room room = room(code, authentication);
        return afterId > 0
            ? chat.findTop50ByRoomAndIdGreaterThanOrderByIdAsc(room, afterId)
            : chat.findTop50ByRoomOrderByIdAsc(room);
    }

    @PostMapping
    public ChatMessage send(@PathVariable String code, @Valid @RequestBody Message message,
                            Authentication authentication) {
        if (message == null || message.text == null || message.text.isBlank()
                || message.text.trim().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Message must contain 1-500 characters");
        }
        Room room = room(code, authentication);
        User user = users.findByUsername(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return chat.save(new ChatMessage(room, user, message.text.trim()));
    }

    private String normalizeCode(String code) {
        String normalized = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z0-9]{6}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid room code");
        }
        return normalized;
    }

    public static class Message {
        @NotBlank
        @Size(max = 500)
        public String text;
    }
}
