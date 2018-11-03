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
import java.util.HashMap;

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
                String filePath = flle.getAbsolutePath();
                InputStream inputStream = new FileInputStream(filePath);
                String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                resources.put(flle.getPath(), content);
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
            String path = "C:\\Users\\mark00x\\Desktop\\English-Reading\\service\\src\\main\\resources";
            File []resources = new File[] {new File(path)};
            LoadFiles(resources);
        }
        catch (Exception ex)
        {
        }
    }
}
