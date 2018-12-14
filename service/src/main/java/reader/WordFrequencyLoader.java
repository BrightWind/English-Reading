package reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reader.Services.ClouldDictionaryService;

public class WordFrequencyLoader {
    List<String> WordFrequencyList = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(ClouldDictionaryService.class);

    public void Load() {
        String path = "/root/resources";
        File top = new File(path);
        if (!top.exists())
        {
            path = "C:\\Users\\mark00x\\Desktop\\English-Reading\\service\\src\\main\\resources";
            top = new File(path);

            if (!top.exists())
            {
                path = "E:\\Projects\\EnglishReader\\English-Reading\\service\\src\\main\\resources";
                top = new File(path);

                if (!top.exists())
                {
                    return;
                }
            }
        }
        else {
            logger.info("Resource exit:" + path);
        }


    }
}
