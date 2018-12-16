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
                QueryWord(profile.strangeWords);

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
                    for (String word: words) {
                        String trimWord = StringHelper.trim(word, ",.?!()-\"").toLowerCase();

                        if (wordBlackListService.Contain(trimWord)) {
                            continue;
                        }

                        int changeIdx = wordFrequencyLoader.WordFrequencyList.indexOf(trimWord);
                        if (changeIdx < settingService.strangeWordLevel.LowLevel) {
                            continue;
                        }

                        if (changeIdx > settingService.strangeWordLevel.HighLevel ||trimWord.length() >= 8)
                        {
                            LongWords.add(trimWord);
                        }
                    }
                }

                DocumentProfile resourceProfile = new DocumentProfile();
                resourceProfile.fileName = fileName;
                resourceProfile.contentLines = contentList;
                resourceProfile.strangeWords = LongWords;
                QueryWord(LongWords);

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

    public void QueryWord(Set<String> LongWords) {
        for (String word : LongWords) {
            try {
                CompletableFuture<WordExplain> qr =  clouldDictionaryService.QueryWord(word);
                qr.thenAccept(wordExplain -> {
                    try {
                        if (wordExplain == null) return;
                        localDictionaryService.Add(wordExplain);
                    } catch (Exception ex) {
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
        }
        catch (Exception ex)
        {
        }
    }
}
