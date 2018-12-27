package reader.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;

import java.util.HashMap;
import java.util.List;

@Service
public class DocumentPresentService {
    private static final Logger logger = LoggerFactory.getLogger(ClouldDictionaryService.class);
    private HashMap documentProfileHashMap = new HashMap<String, DocumentProfile>();


    @Autowired
    DocumentProfileDao documentProfileDao;

    public void Load() {
        List<DocumentProfile> profileList = documentProfileDao.Get();
        for (DocumentProfile item: profileList) {
            if (documentProfileHashMap.containsKey(item.fileName)) {
                logger.info(String.format("document profile exist:%s", item.fileName));
                continue;
            }

            documentProfileHashMap.put(item.fileName, item);
        }
    }

    public void Add() {

    }

    public void Get() {

    }


}
