package reader.Services.DocLoadService;

import org.apache.commons.io.IOUtils;
import reader.Model.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LocalDocLoader extends IDocLoader {
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

        if (file.exists()) {
            try {
                String filePath = file.getAbsolutePath();
                InputStream inputStream = new FileInputStream(filePath);
                String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

                Document document = new Document();
                document.url = filePath;
                document.tag = fileName;
                document.content = content;
                documentDao.Save(document);
                file.delete();

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
