package reader;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reader.Helper.ChineseChecker;
import reader.Helper.StaticConfigration;
import reader.Helper.StringHelper;
import reader.Model.*;
import reader.Services.ClouldDictionaryService;
import reader.Services.LocalDictionaryService;
import reader.Services.SettingService;
import reader.Services.WordBlackListService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class MarqueeLoader {
    private static final Logger logger = LoggerFactory.getLogger(ClouldDictionaryService.class);

    @Value("classpath")
    Resource resourcePath;

    @Autowired
    DocumentProfileDao documentProfileDao;

    @Autowired
    ClouldDictionaryService clouldDictionaryService;

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    WordBlackListService wordBlackListService;

    @Autowired
    SettingService settingService;

    @Autowired
    WordFrequencyLoader wordFrequencyLoader;


    public HashMap resources = new HashMap<String, DocumentProfile>();

    public String GetOriginalWord(String text) {
        String pattern = "（([a-zA-Z]+)的";
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(text);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    public void LoadFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                logger.info("Directory: " + file.getName());
                LoadFiles(file.listFiles()); // Calls same method again.
            } else {
                ReadFile(file);
            }
        }
    }

    private Set<String> lowSet = new HashSet();
    private Set<String> HighSet = new HashSet();

    public boolean IsStrangeWord(String word) {
        if (wordBlackListService.Contain(word)) {
            return false;
        }

        int changeIdx = wordFrequencyLoader.WordFrequencyList.indexOf(word);
        if (changeIdx != -1 && changeIdx < settingService.strangeWordLevel.LowLevel) {
            return false;
        }

        if (changeIdx > settingService.strangeWordLevel.HighLevel) {
            return true;
        }

        if (word.length() < 7) {
            lowSet.add(word);
            return false;
        }
        else {
            HighSet.add(word);
            return true;
        }
    }

    public void ReadFile(File flle)
    {
        String fileName = flle.getName();
        try
        {
            if (fileName.equals("application.properties") || fileName.equals("word_frequency")) {
                return;
            }

            DocumentProfile profile = documentProfileDao.GetByName(fileName);
            if (profile != null)
            {
                logger.info(String.format("Get file From mango{%s}{%s}", profile.id, fileName));
                resources.put(profile.id, profile);
                QueryWord(profile, profile.strangeWords);

                return;
            }
        }
        catch (Exception ex)
        {
            //not exist
        }

        if (flle.exists())
        {
            try
            {
                List<String> contentList = new ArrayList<>();
                StringBuffer content = new StringBuffer();
                Set<String> LongWords = new HashSet<>();

                String filePath = flle.getAbsolutePath();
                InputStream inputStream = new FileInputStream(filePath);
                String temp = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

                //remove start string
                String start = "字幕信息";
                int idx = temp.indexOf(start);
                if (-1 != idx) {
                    temp = temp.substring(idx + start.length());
                }

                start = "前情回顾";
                idx = temp.indexOf(start);
                if (-1 != idx) {
                    temp = temp.substring(idx + start.length());
                }

                start = "最新影视";
                idx = temp.indexOf(start);
                if (-1!= idx) {
                    temp = temp.substring(idx + start.length());
                }

                start = "}总监";
                idx = temp.indexOf(start);
                if (-1!= idx) {
                    temp = temp.substring(idx + start.length());
                }

                //detect the line splitter
                String splitter = "\r\n";
                if (temp.indexOf(splitter) == -1)
                {
                    splitter = "\n";
                }

                String []lines = temp.split(splitter);
                String pattern = "\\d\\d:\\d\\d:\\d\\d";
                Pattern regex = Pattern.compile(pattern);
                for (String line : lines) {
                    line.trim();

                    //it may be empty line
                    if (line.length() < 2)
                    {
                        content.append("\n");       //keep the line
                        continue;
                    }

                    if (line.indexOf("{\\an") != -1)
                    {
                        continue;
                    }

                    //it is too small, should no be a word
                    if (line.length() < 5){
                        continue;
                    }

                    //it should be time string
                    Matcher result = regex.matcher(line);
                    if (result.find() && result.find()) //at least match twice
                    {
                        continue;
                    }

                    //collect meaningful content
                    content.append(line).append("\n");
                    contentList.add(line);

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

                DocumentProfile resourceProfile = new DocumentProfile();
                resourceProfile.fileName = fileName;
                resourceProfile.contentLines = contentList;
                resourceProfile.strangeWords = LongWords;
                QueryWord(resourceProfile, LongWords);

                documentProfileDao.Save(resourceProfile);
                resources.put(resourceProfile.id, resourceProfile);
                logger.info(String.format("Get file From local{%s}{%s}", resourceProfile.id, fileName));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private String GetOrgWordFromList(List<String> explain_list) {
        for (String line : explain_list) {
            String org = GetOriginalWord(line);
            if (org != null) {
                return org;
            }
        }

        return null;
    }

    public void QueryWord(DocumentProfile resourceProfile, Set<String> LongWords) {
        for (String word : LongWords) {
            try {
                CompletableFuture<WordExplain> qr =  clouldDictionaryService.QueryWord(word);
                qr.thenAccept(wordExplain -> {
                    try {
                        if (wordExplain == null) return;

                        String org = GetOrgWordFromList(wordExplain.explain_list);
                        if (org == null) {
                            localDictionaryService.Add(wordExplain);
                            return;
                        }

                        resourceProfile.strangeWords.remove(word);
                        documentProfileDao.DeleteWord(resourceProfile.id, word);
                        if (IsStrangeWord(org)) {
                            try
                            {
                                CompletableFuture<WordExplain> tqr =  clouldDictionaryService.QueryWord(org);
                                tqr.thenAccept(wordExplain1 -> {
                                    resourceProfile.strangeWords.add(word);
                                    documentProfileDao.AddWord(resourceProfile.id, word);
                                });
                            }
                            catch (Exception ex) {

                            }
                        }
                    }
                    catch (Exception ex) {

                    }
                });
            }
            catch (Exception ex) {
            }
        }
    }

    @PostConstruct
    public void load ()
    {
        settingService.init();
        wordFrequencyLoader.Init();
        try
        {
            String path = StaticConfigration.ResourcePath();
            if (path == null) return;
            File []resources = new File[] {new File(path)};
            LoadFiles(resources);

            System.out.printf("----------------low set----------------\n");
            lowSet.forEach(word -> {
                System.out.printf("s s %s\n", word);
            });

            System.out.printf("\n\n----------------high set----------------\n");
            HighSet.forEach(word -> {
                System.out.printf("s s %s\n", word);
            });
        }
        catch (Exception ex)
        {
        }
    }
}
