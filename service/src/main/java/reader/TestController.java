package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reader.Services.BlackWhiteWordService;
import reader.Services.DocumentLoadingService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@CrossOrigin(value = "*")
@RestController
public class TestController {
    @Autowired
    DocumentLoadingService documentLoadingService;

    @Autowired
    BlackWhiteWordService blackWhiteWordService;

    @RequestMapping(value = "test/strange/capture")
    public Set<String> CaptureWord(@RequestParam String content) {
        List<String> lines;
        Set<String> words;

        lines = blackWhiteWordService.CaptureLines(content);
        lines = blackWhiteWordService.CaptureValidLines(lines);
        words = blackWhiteWordService.CaptureWords(lines);
        words = blackWhiteWordService.CaptureStrangeWord(words);

        return words;
    }


}
