package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reader.Model.WordBlackList;
import reader.Model.WordBlackListDao;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class WordBlackListService {
    private WordBlackList wordBlackList;

    @Autowired
    WordBlackListDao wordBlackListDao;

    @PostConstruct
    private void init() {
        List<WordBlackList> wordBlackListArr = wordBlackListDao.Get();
        if (wordBlackListArr == null || wordBlackListArr.size() == 0) {
            wordBlackList = new WordBlackList();
            wordBlackListDao.Save(wordBlackList);
        } else {
            wordBlackList  = wordBlackListArr.get(0);
        }
    }

    public boolean Contain(String word) {
        return wordBlackList.blackList.contains(word);
    }

    public void Add(String word) {
        wordBlackList.blackList.add(word);
        wordBlackListDao.AddWord(wordBlackList.id, word);
    }

}
