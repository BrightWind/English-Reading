package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reader.Model.WordBlackList;
import reader.Model.WordBlackListDao;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BlackWhiteWordService {
    private WordBlackList wordBlackList;
    private Lock whiteLock = new ReentrantLock();
    private Lock blackLock = new ReentrantLock();

    @Autowired
    WordBlackListDao wordBlackListDao;

    public void Load()  {
        List<WordBlackList> blackList = wordBlackListDao.Get();
        if (blackList == null || blackList.size() == 0) {
            wordBlackList = new WordBlackList();
            wordBlackListDao.Save(wordBlackList);
        }
        else {
            wordBlackList = blackList.get(0);
        }
    }

    private boolean Contain(String word, Lock lock, Set<String> wordList) {
        boolean isContain;

        lock.lock();
        isContain = wordList.contains(word);
        lock.unlock();

        return isContain;
    }

    public boolean ContainInWhite(String word) {
       return Contain(word, whiteLock, wordBlackList.whiteList);
    }

    public boolean ContainInBlack(String word) {
        return Contain(word, blackLock, wordBlackList.blackList);
    }

    private void Add(String word, Lock lock, Set<String> wordList) {
        lock.lock();
        wordList.add(word);
        lock.unlock();
    }

    public void AddToWhite(String word) {
        Add(word, whiteLock, wordBlackList.whiteList);
        wordBlackListDao.AddToWhite(wordBlackList.id, word);
    }

    public void AddToBlack(String word) {
        Add(word, blackLock, wordBlackList.blackList);
        wordBlackListDao.AddToBlack(wordBlackList.id, word);
    }

    private void AddList(List<String> list, Lock lock, Set<String> wordList) {
        lock.lock();
        wordList.addAll(list);
        lock.unlock();
    }

    public void AddListToWhite(List<String> list) {
        AddList(list, whiteLock, wordBlackList.whiteList);
        wordBlackListDao.AddListToWhite(wordBlackList.id, list);
    }

    public void AddListToBlack(List<String> list) {
        AddList(list, blackLock, wordBlackList.blackList);
        wordBlackListDao.AddListToBlack(wordBlackList.id, list);
    }

    protected void Remove(String word, Lock lock, Set<String> wordList) {
        lock.lock();
        if (wordList.contains(word)) {
            wordList.remove(word);
        }
        lock.unlock();
    }

    public void RemoveFromWhite(String word) {
        Remove(word, whiteLock, wordBlackList.whiteList);
    }

    public void RemoveFromBlack(String word) {
        Remove(word, blackLock, wordBlackList.blackList);
    }
}
