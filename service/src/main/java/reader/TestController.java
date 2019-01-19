package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reader.Helper.StringHelper;
import reader.Services.WhiteListService;
import reader.Services.ResourceLoadingService;

import java.util.List;
import java.util.Set;


@CrossOrigin(value = "*")
@RestController
public class TestController {
    @Autowired
    ResourceLoadingService documentLoadingService;

    @Autowired
    WhiteListService blackWhiteWordService;

    @RequestMapping(value = "test/strange/capture")
    public Set<String> CaptureWord(@RequestParam String content) {
        List<String> lines;
        Set<String> words;

        lines = blackWhiteWordService.CaptureLines(content);
        lines = StringHelper.CaptureEnglishSentences(lines);
        words = StringHelper.SplitToWords(lines);
        words = blackWhiteWordService.CaptureStrangeWord(words);

        return words;
    }


}
