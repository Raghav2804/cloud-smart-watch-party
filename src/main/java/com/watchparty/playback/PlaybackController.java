package com.watchparty.playback;

import com.watchparty.auth.User;
import com.watchparty.auth.UserRepository;
import com.watchparty.room.Room;
import com.watchparty.room.RoomRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/rooms/{code}/playback")
public class PlaybackController {
    private final RoomRepository rooms;
    private final UserRepository users;

    public PlaybackController(RoomRepository rooms, UserRepository users) {
        this.rooms = rooms;
        this.users = users;
    }

    private Room room(String code, UserDetails details) {
        Room room = rooms.findByCode(normalizeCode(code))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        User user = users.findByUsername(details.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!room.hasMember(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Join this room first");
        }
        return room;
    }

    @GetMapping
    public Playback state(@PathVariable String code, @AuthenticationPrincipal UserDetails details) {
        Room room = room(code, details);
        double position = room.getPositionSeconds();
        if (room.isPlaying()) {
            long elapsedMillis = Math.max(0, Duration.between(room.getUpdatedAt(), Instant.now()).toMillis());
            position += elapsedMillis / 1000.0;
        }
        return new Playback(room.isPlaying(), position, room.getUpdatedAt().toEpochMilli());
    }

    @PostMapping
    public Playback update(@PathVariable String code, @RequestBody Request request,
                           @AuthenticationPrincipal UserDetails details) {
        if (request == null || !Double.isFinite(request.positionSeconds()) || request.positionSeconds() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Position must be a finite non-negative number");
        }
        Room room = room(code, details);
        User user = users.findByUsername(details.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!room.isHost(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host can update playback");
        }
        room.setPlaying(request.playing());
        room.setPositionSeconds(request.positionSeconds());
        rooms.save(room);
        return new Playback(room.isPlaying(), room.getPositionSeconds(), room.getUpdatedAt().toEpochMilli());
    }

    private String normalizeCode(String code) {
        String normalized = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z0-9]{6}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid room code");
        }
        return normalized;
    }

    public record Request(boolean playing, double positionSeconds) {}
    public record Playback(boolean playing, double positionSeconds, long updatedAt) {}
}
