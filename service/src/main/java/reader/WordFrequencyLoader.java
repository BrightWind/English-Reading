package reader;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import reader.Helper.StaticConfigration;
import reader.Helper.TextLoader;

@Service
public class WordFrequencyLoader {
    public List<String> WordFrequencyList;

    public void Init() {
        List<String> content_list = new ArrayList<>();
        try
        {
            String filePath = StaticConfigration.ResourcePath() + "\\word_frequency";
            List<String> temp_list = TextLoader.FileToList(filePath);
            for (String line: temp_list) {
                String []temp = line.split("\t");
                if (temp.length < 3) {
                    continue;
                }
                content_list.add(temp[2]);
             }
        }
        catch (Exception ex) {
        }

        WordFrequencyList = content_list;
    }
}
