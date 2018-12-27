package reader.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DocumentPresentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentPresentService.class);
    private HashMap documentProfileHashMap = new HashMap<String, DocumentProfile>();
    private Lock lock = new ReentrantLock();

    @Autowired
    DocumentProfileDao documentProfileDao;

    public void Load() {
        List<DocumentProfile> profileList = documentProfileDao.Get();
        if (profileList == null) {
            return;
        }
        for (DocumentProfile item: profileList) {
            if (documentProfileHashMap.containsKey(item.fileName)) {
                logger.info(String.format("document profile exist:%s", item.fileName));
                continue;
            }

            documentProfileHashMap.put(item.fileName, item);
        }
    }

    public void Add(DocumentProfile documentProfile) {
        lock.lock();
        documentProfileHashMap.put(documentProfile.fileName, documentProfile);
        lock.unlock();
        documentProfileDao.Save(documentProfile);
    }

    public List<DocumentProfile> Get() {
        List<DocumentProfile> documentProfileList = new ArrayList<>();
        lock.lock();
        documentProfileHashMap.forEach((key,value)->{
            documentProfileList.add((DocumentProfile)value);
        });
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
}
