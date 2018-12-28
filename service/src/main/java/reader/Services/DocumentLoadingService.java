package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Helper.ChineseChecker;
import reader.Helper.StaticConfigration;
import reader.Helper.StringHelper;
import reader.Model.Document;
import reader.Model.DocumentDao;
import reader.Model.DocumentProfile;
import reader.Services.DocLoadService.IDocLoader;
import reader.Services.DocLoadService.ILoaderObserver;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentLoadingService implements ILoaderObserver {
    protected Queue<Document> task = new ArrayDeque<>(10);

    @Autowired
    BlackWhiteWordService blackWhiteWordService;

    @Autowired
    WordQueryingService wordQueryingService;

    @Autowired
    DocumentPresentService documentPresentService;

    @Autowired
    DocumentDao documentDao;

    public void LoadAsync() {
        String path = StaticConfigration.ResourcePath();
        IDocLoader iLocalLoader = IDocLoader.Create(0, this);
        iLocalLoader.documentDao = documentDao;
        iLocalLoader.Load(path);
    }

    @Override
    public void OnDocLoaded(Document document) {
        task.add(document);
        ConvertToPresentDoc(document);
    }

    public List<String> CaptureLines(String content) {
        String splitter = "\r\n";
        if (content.indexOf(splitter) == -1)
        {
            splitter = "\n";
        }

        String []lines = content.split(splitter);
        return Arrays.asList(lines);
    }

    public List<String> CaptureValidLines(List<String> lines) {
        List<String> newLines = new ArrayList<>();

        String pattern = "\\d\\d:\\d\\d:\\d\\d";
        Pattern regex = Pattern.compile(pattern);
        lines.forEach(line -> {
            line.trim();

            if (line.indexOf("{\\") != -1)
            {
                return;
            }

            //it is too small, should no be a word
            if (line.length() < 5){
                return;
            }

            //it should be time string
            Matcher result = regex.matcher(line);
            if (result.find() && result.find()) //at least match twice to ensure it was not a content line
            {
                return;
            }

            newLines.add(line);
        });
        return newLines;
    }

    public Set<String> CaptureWords(List<String> lines) {
        Set<String> word_set = new HashSet<>();

        for (String line : lines) {
            //it should be chinese
            if (ChineseChecker.hasChinese(line))
            {
                continue;
            }

            //collect long word
            String words[] = line.split(" ");
            ArrayDeque<String> wordQueue = new ArrayDeque(Arrays.asList(words));
            while (wordQueue.size() > 0) {
                String word = wordQueue.pop();
                String trimWord = StringHelper.trim(word, ",.?!()-\"").toLowerCase();

                if (trimWord.isEmpty()) {
                    continue;
                }

                if (trimWord.contains("'s")
                        ||trimWord.contains("'t")
                        ||trimWord.contains("'v")
                        ||trimWord.contains("'d")
                        ||trimWord.contains("'re")) {
                    continue;
                }

//                if (trimWord.indexOf(".") != -1
//                    || trimWord.indexOf("-") != -1
//                    || trimWord.indexOf("/") != -1) {
//                    continue;
//                }

                trimWord.toLowerCase();

                word_set.add(trimWord);
            }
        }

        return word_set;
    }

    public Set<String> CaptureStrangeWord(Set<String> words) {
        Set<String> strangeSet = new HashSet<>();
        words.forEach(word -> {
            if (blackWhiteWordService.ContainInWhite(word)) {
                strangeSet.add(word);
                return;
            }

            if (blackWhiteWordService.ContainInBlack(word)) {
                return;
            }

            if (word.length() < 7) {
                blackWhiteWordService.AddToBlack(word);
            }
            else {
                blackWhiteWordService.AddToWhite(word);
                strangeSet.add(word);
            }
        });

        return strangeSet;
    }

    public void ConvertToPresentDoc(Document document) {
        List<String> contents;
        Set<String> strangeWords;

        contents = CaptureLines(document.content);
        contents = CaptureValidLines(contents);
        strangeWords = CaptureWords(contents);
        strangeWords = CaptureStrangeWord(strangeWords);

        DocumentProfile resourceProfile = new DocumentProfile();
        resourceProfile.fileName = document.tag;
        resourceProfile.contentLines = contents;
        resourceProfile.strangeWords = strangeWords;

        CompletableFuture future = wordQueryingService.QueryWordAsync(resourceProfile.strangeWords);
        future.thenRun(()->{
            documentPresentService.Add(resourceProfile);
        });
    }
}
