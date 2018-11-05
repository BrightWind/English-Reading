package reader;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StaticResource {
    @Value("classpath")
    Resource resourcePath;

    public HashMap resources = new HashMap<String, String>();

    public void LoadFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                LoadFiles(file.listFiles()); // Calls same method again.
            } else {
                ReadFile(file);
            }
        }
    }

    public void ReadFile(File flle)
    {
        if (flle.exists())
        {
            try
            {
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

                    //it is too small, should no be a work
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

                    //collect long word
                    String words[] = line.split(" ");
                    for (String word: words) {
                        if (word.length() > 8)
                        {
                            LongWords.add(word);
                        }
                    }
                }

                resources.put(UUID.randomUUID().toString(), content.toString());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @PostConstruct
    public void init ()
    {
        try
        {
            String path = "/root/resources";
            File top = new File(path);
            if (!top.exists())
            {
                path = "C:\\Users\\mark00x\\Desktop\\English-Reading\\service\\src\\main\\resources";
                top = new File(path);

                if (!top.exists())
                {
                    return;
                }
            }

            File []resources = new File[] {top};
            LoadFiles(resources);
        }
        catch (Exception ex)
        {
        }
    }
}
