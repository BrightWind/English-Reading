package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Helper.StaticConfigration;
import reader.Model.Document;
import reader.Model.DocumentDao;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;
import reader.Services.DocLoadService.IDocLoader;
import reader.Services.DocLoadService.ILoaderObserver;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class DocumentLoadingService implements ILoaderObserver {
    @Autowired
    BlackWhiteWordService blackWhiteWordService;

    @Autowired
    WordQueryingService wordQueryingService;

    @Autowired
    DocumentPresentService documentPresentService;

    @Autowired
    DocumentDao documentDao;

    @Autowired
    DocumentProfileDao documentProfileDao;

    public void LoadAsync() {
        String path = StaticConfigration.ResourcePath();
        IDocLoader iLocalLoader = IDocLoader.Create(0, this);
        iLocalLoader.documentDao = documentDao;
        iLocalLoader.Load(path);
    }

    @Override
    public void OnDocLoaded(Document document) {
        DocumentProfile documentProfile = documentProfileDao.GetByName(document.url);
        if (documentProfile == null) {
            ConvertToPresentDoc(document);
        }
    }


    public void ConvertToPresentDoc(Document document) {
        List<String> contents;
        Set<String> strangeWords;

        contents = blackWhiteWordService.CaptureLines(document.content);
        contents = blackWhiteWordService.CaptureValidLines(contents);
        strangeWords = blackWhiteWordService.CaptureWords(contents);
        strangeWords = blackWhiteWordService.CaptureStrangeWord(strangeWords);

        DocumentProfile resourceProfile = new DocumentProfile();
        resourceProfile.fileName = document.tag;
        resourceProfile.url = document.url;
        resourceProfile.contentLines = contents;
        resourceProfile.strangeWords = strangeWords;

        CompletableFuture future = wordQueryingService.QueryWordAsync(resourceProfile.strangeWords);
        future.thenRun(()->{
            documentPresentService.Add(resourceProfile);
        });
    }
}
