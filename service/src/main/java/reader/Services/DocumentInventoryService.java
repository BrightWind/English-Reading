package reader.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Helper.StringHelper;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Service
public class DocumentInventoryService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentInventoryService.class);
    private HashMap documentProfileHashMap = new HashMap<String, DocumentProfile>();
    private Lock lock = new ReentrantLock();

    private boolean bReady = false;

    @Autowired
    DocumentProfileDao documentProfileDao;

    @Autowired
    WhiteListService blackWhiteWordService;

    @Autowired
    WordQueryingService wordQueryingService;

    @Autowired
    WordFrequencyService wordFrequencyService;

    public void Load(Consumer<List<DocumentProfile>> onLoaded) {
        List<DocumentProfile> profileList = documentProfileDao.Get();
        if (profileList == null) {
            return;
        }

        profileList.forEach(documentProfile -> {
            if (documentProfileHashMap.containsKey(documentProfile.id)) {
                logger.info(String.format("document profile exist:%s", documentProfile.fileName));
                return;
            }

            documentProfileHashMap.put(documentProfile.id, documentProfile);
        });

        if (onLoaded != null) onLoaded.accept(profileList);
    }

    public void Add(DocumentProfile documentProfile) {
        lock.lock();
        documentProfileHashMap.put(documentProfile.id, documentProfile);
        lock.unlock();
        documentProfileDao.Save(documentProfile);
    }

    public List<DocumentProfile> Get() {
        if (!bReady) return null;

        List<DocumentProfile> documentProfileList = new ArrayList<>();
        lock.lock();
        documentProfileHashMap.forEach((key,value)-> documentProfileList.add((DocumentProfile)value));
        lock.unlock();
        return documentProfileList;
    }

    public DocumentProfile Get(String id) {
        DocumentProfile profile = null;
        lock.lock();
        if (documentProfileHashMap.containsKey(id)) {
            profile = (DocumentProfile) documentProfileHashMap.get(id);
        }
        lock.unlock();
        return profile;
    }

    public void UpdateIndex(DocumentProfile documentProfile, int position) {
        documentProfile.rPosition = position;
        documentProfileDao.UpdateIndex(documentProfile.id, position);
    }

    public void RemoveWord(DocumentProfile documentProfile, String word) {
        documentProfile.strangeWords.remove(word);
        documentProfileDao.DeleteWord(documentProfile.id, word);
        blackWhiteWordService.RemoveFromWhite(word);
    }

    public void AddWord(DocumentProfile documentProfile, String word) {
        documentProfile.strangeWords.add(word);
        documentProfileDao.AddWord(documentProfile.id, word);
        blackWhiteWordService.AddToWhiteList(word);
    }

    public void SaveStrangeWords(DocumentProfile documentProfile, Set<String> word_set) {
        documentProfile.strangeWords = word_set;
        documentProfileDao.SaveStrangeWord(documentProfile.id, word_set);
        blackWhiteWordService.AddListToWhite(word_set);
    }

    public void Ready() {
        bReady = true;
    }
}
