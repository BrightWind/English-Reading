package reader.Services.DocLoadService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reader.Model.Document;
import reader.Services.ResourceLoadingService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LocalDocLoader extends IDocLoader {
    private static final Logger logger = LoggerFactory.getLogger(LocalDocLoader.class);

    public LocalDocLoader(ILoaderObserver iLoaderObserver) {
        super(iLoaderObserver);
    }

    protected void LoadFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                LoadFiles(file.listFiles()); // Calls same method again.
            } else {
                LoadFile(file);
            }
        }
    }

    protected void LoadFile(File file) {
        String fileName = file.getName();
        if (fileName.equals("application.properties") || fileName.equals("word_frequency")) {
            return;
        }

        if (fileName.endsWith(".ass") || fileName.contains(".简体.") || fileName.contains("繁体")) {
            return;
        }

        if (file.exists()) {
            try {
                String filePath = file.getAbsolutePath();
                Document document = documentDao.GetByURL(filePath);
                if (document == null) {
                    InputStream inputStream = new FileInputStream(filePath);
                    String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    document = new Document();
                    document.url = filePath;
                    document.tag = fileName;
                    document.content = content;
                    documentDao.Save(document);
                    file.delete();
                }

                logger.info(String.format("loading file: %s", fileName));
                if (mLoaderObserver != null) mLoaderObserver.OnDocLoaded(document);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void Load(String path){
        File []files = new File[] {new File(path)};
        LoadFiles(files);
    }
}
