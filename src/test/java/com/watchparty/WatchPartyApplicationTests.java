package com.watchparty;

import static org.assertj.core.api.Assertions.assertThat;

import com.watchparty.auth.UserRepository;
import com.watchparty.chat.ChatRepository;
import com.watchparty.room.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class WatchPartyApplicationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository users;

    @Autowired
    private RoomRepository rooms;

    @Autowired
    private ChatRepository messages;

    @Test
    void applicationContextLoadsWithCoreRepositories() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(users).isNotNull();
        assertThat(rooms).isNotNull();
        assertThat(messages).isNotNull();
    }

    @Test
    void passwordEncoderDoesNotStorePlaintext() {
        String password = "correct horse battery staple";
        String encoded = passwordEncoder.encode(password);

        assertThat(encoded).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, encoded)).isTrue();
        assertThat(passwordEncoder.matches("wrong password", encoded)).isFalse();
    }
}
