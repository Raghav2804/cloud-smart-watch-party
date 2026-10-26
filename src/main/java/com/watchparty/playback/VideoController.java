package com.watchparty.playback;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {
    @GetMapping("/api/videos")
    public List<Video> videos() {
        return List.of(
            new Video(1L, "Big Buck Bunny", "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            new Video(2L, "Elephants Dream", "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        );
    }

    public record Video(Long id, String title, String videoUrl) {
    }
}
