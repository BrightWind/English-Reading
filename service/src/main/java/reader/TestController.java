package reader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reader.Services.DocPresentService.DocObserver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class TestController {
    @RequestMapping(value = "test/strange/capture")
    public Set<String> CaptureWord(@RequestParam String content) {
        List<String> lines;
        Set<String> words;
        Set<String> whiteSet = new HashSet<>();
        Set<String> blackSet = new HashSet<>();

        lines = DocObserver.CaptureLines(content);
        lines = DocObserver.CaptureValidLines(lines);
        words = DocObserver.CaptureStrangeWord(lines, whiteSet, blackSet);

        return words;
    }


}
