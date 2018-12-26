package reader.Services.DocPresentService;

import org.springframework.stereotype.Component;
import reader.Helper.ChineseChecker;
import reader.Helper.StringHelper;
import reader.Model.Document;
import reader.Model.DocumentProfile;
import reader.Services.DocLoadService.ILoaderObserver;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DocObserver implements ILoaderObserver {
    protected Queue<Document> task = new ArrayDeque<>(10);

    @Override
    public void OnDocLoaded(Document document) {
        task.add(document);
        ConvertToPresentDoc(document);
    }

    public static List<String> CaptureLines(String content) {
        String splitter = "\r\n";
        if (content.indexOf(splitter) == -1)
        {
            splitter = "\n";
        }

        String []lines = content.split(splitter);
        return Arrays.asList(lines);
    }

    public static List<String> CaptureValidLines(List<String> lines) {
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

    public static Set<String> CaptureStrangeWord(List<String> lines, Set<String> whiteList, Set<String> blackList) {
        Set<String> strangeSet = new HashSet<>();

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
                    ||trimWord.contains("'d")) {
                    continue;
                }

                if (whiteList.contains(word)) {
                    strangeSet.add(word);
                    continue;
                }

                if (blackList.contains(word)) {
                    continue;
                }

                if (word.length() < 7) {
                    blackList.add(word);
                }
                else {
                    whiteList.add(word);
                    strangeSet.add(word);
                }
            }
        }

        return strangeSet;
    }

    public void ConvertToPresentDoc(Document document) {
        List<String> contents;
        Set<String> strangeWords;
        Set<String> whiteList = new HashSet<>();
        Set<String> blackList = new HashSet<>();
        contents = CaptureLines(document.content);
        contents = CaptureValidLines(contents);
        strangeWords = CaptureStrangeWord(contents, whiteList, blackList);

        DocumentProfile resourceProfile = new DocumentProfile();
        resourceProfile.fileName = document.tag;
        resourceProfile.contentLines = contents;
        resourceProfile.strangeWords = strangeWords;
        //QueryWord(resourceProfile, LongWords);

        //documentProfileDao.Save(resourceProfile);
        //resources.put(resourceProfile.id, resourceProfile);
        //logger.info(String.format("Get file From local{%s}{%s}", resourceProfile.id, fileName));
    }
}
