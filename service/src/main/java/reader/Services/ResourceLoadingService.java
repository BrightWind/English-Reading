package reader.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Helper.StaticConfigration;
import reader.Helper.StringHelper;
import reader.Model.Document;
import reader.Model.DocumentDao;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;
import reader.Services.DocLoadService.IDocLoader;
import reader.Services.DocLoadService.ILoaderObserver;

import java.util.*;

@Service
public class ResourceLoadingService implements ILoaderObserver {
    private static final Logger logger = LoggerFactory.getLogger(ResourceLoadingService.class);

    @Autowired
    WhiteListService blackWhiteWordService;

    @Autowired
    WordQueryingService wordQueryingService;

    @Autowired
    DocumentInventoryService documentPresentService;

    @Autowired
    DocumentDao documentDao;

    @Autowired
    DocumentProfileDao documentProfileDao;

    @Autowired
    WordFrequencyService wordFrequencyService;

    public void StartAsync() {
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
        List<String> contents = blackWhiteWordService.CaptureLines(document.content);
        contents = StringHelper.CaptureEnglishSentences(contents);
        Set<String> words = StringHelper.SplitToWords(contents);
        wordFrequencyService.AddWord(words);
        logger.info(String.format("wordFrequencyService size:%d", wordFrequencyService.GenerateWordRankList().size()));

        Set<String> strangeWords = blackWhiteWordService.CaptureStrangeWord(words);
        wordQueryingService.QueryWordAsync(strangeWords);

        //strangeWords = blackWhiteWordService.CaptureStrangeWord(words);

        DocumentProfile resourceProfile = new DocumentProfile();
        resourceProfile.fileName = document.tag;
        resourceProfile.url = document.url;
        resourceProfile.contentLines = contents;
        documentPresentService.Add(resourceProfile);

        /*
        resourceProfile.strangeWords = strangeWords;
        CompletableFuture future = wordQueryingService.QueryWordAsync(resourceProfile.strangeWords);
        future.thenRun(()->{
            documentPresentService.Add(resourceProfile);
        });
        */
    }
}
