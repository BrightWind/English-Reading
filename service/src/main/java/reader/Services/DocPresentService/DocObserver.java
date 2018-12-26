package reader.Services.DocPresentService;

import reader.Helper.ChineseChecker;
import reader.Helper.StringHelper;
import reader.Model.Document;
import reader.Model.DocumentProfile;
import reader.Services.DocLoadService.ILoaderObserver;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocObserver implements ILoaderObserver {
    protected Queue<Document> task = new ArrayDeque<>(10);

    @Override
    public void OnDocLoaded(Document document) {
        task.add(document);
    }

    protected List<String> StringToLines(String content) {
        String splitter = "\r\n";
        if (content.indexOf(splitter) == -1)
        {
            splitter = "\n";
        }

        String []lines = content.split(splitter);
        return Arrays.asList(lines);
    }

    protected List<String> RemoveInvaildLines(List<String> lines) {
        List<String> newLines = new ArrayList<>();

        String pattern = "\\d\\d:\\d\\d:\\d\\d";
        Pattern regex = Pattern.compile(pattern);
        lines.forEach(line -> {
            line.trim();

            if (line.indexOf("{\\an") != -1)
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

    protected void CaptureStrangeWord(List<String> lines) {
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

                if (trimWord.contains("'s")) {
                    trimWord = trimWord.replace("'s", "");
                }

                if (IsStrangeWord(trimWord)) {
                    LongWords.add(trimWord);
                }
            }
        }
    }

    public void An(Document document) {
        String content = document.content;




        DocumentProfile resourceProfile = new DocumentProfile();
        resourceProfile.fileName = fileName;
        resourceProfile.contentLines = contentList;
        resourceProfile.strangeWords = LongWords;
        QueryWord(resourceProfile, LongWords);

        documentProfileDao.Save(resourceProfile);
        resources.put(resourceProfile.id, resourceProfile);
        logger.info(String.format("Get file From local{%s}{%s}", resourceProfile.id, fileName));
    }
}
