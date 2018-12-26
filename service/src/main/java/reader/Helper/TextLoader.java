package reader.Helper;

import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextLoader {
    public static String FileToString(String FilePath) throws Exception {
        File file = new File(FilePath);
        if (file.exists())
        {
            List<String> contentList = new ArrayList<>();
            InputStream inputStream = new FileInputStream(FilePath);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        else {
            return null;
        }
    }

    public static List<String> FileToList(String FilePath) throws Exception {
        List<String> contentList = null;
        String fileContent = FileToString(FilePath);
        if (fileContent != null) {
            contentList = new ArrayList<>();
            String splitter = "\r\n";
            if (fileContent.indexOf(splitter) == -1)
            {
                splitter = "\n";
            }

            String []lines = fileContent.split(splitter);
            for (String line : lines) {
                line.trim();
                contentList.add(line);
            }
        }
        return contentList;
    }
}
