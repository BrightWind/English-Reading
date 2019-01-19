package reader.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reader.Helper.ChineseChecker;
import reader.Helper.StringHelper;
import reader.Model.WordBlackList;
import reader.Model.WordBlackListDao;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WhiteListService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceLoadingService.class);

    private WordBlackList wordBlackList;
    private Lock whiteLock = new ReentrantLock();
    private Lock blackLock = new ReentrantLock();
    public int WordGrate = 2000;

    @Autowired
    WordBlackListDao wordBlackListDao;

    @Autowired
    WordFrequencyService wordFrequencyService;

    public void Load()  {
        List<WordBlackList> blackList = wordBlackListDao.Get();
        if (blackList == null || blackList.size() == 0) {
            wordBlackList = new WordBlackList();
            wordBlackListDao.Save(wordBlackList);
        }
        else {
            wordBlackList = blackList.get(0);
        }
        logger.info(String.format("BlackWhiteWordService black--:%d, white:%d", wordBlackList.blackList.size(), wordBlackList.whiteList.size()));
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

    public void AddToWhiteList(String word) {
        Add(word, whiteLock, wordBlackList.whiteList);
        wordBlackListDao.AddToWhite(wordBlackList.id, word);
    }

    public void AddToBlackList(String word) {
        Add(word, blackLock, wordBlackList.blackList);
        wordBlackListDao.AddToBlack(wordBlackList.id, word);
    }

    private void AddList(Set<String> list, Lock lock, Set<String> wordList) {
        lock.lock();
        wordList.addAll(list);
        lock.unlock();
    }

    public void AddListToWhite(Set<String> list) {
        AddList(list, whiteLock, wordBlackList.whiteList);
        wordBlackListDao.AddListToWhite(wordBlackList.id, list);
    }

    public void AddListToBlack(Set<String> list) {
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

    public List<String> CaptureLines(String content) {
        String splitter = "\r\n";
        if (content.indexOf(splitter) == -1)
        {
            splitter = "\n";
        }

        String []lines = content.split(splitter);
        return Arrays.asList(lines);
    }

    public Set<String> CaptureStrangeWord(Set<String> words) {
        List<String> wordRankList = wordFrequencyService.GenerateWordRankList();

        Set<String> strangeSet = new HashSet<>();
        words.forEach(word -> {
            if (ContainInWhite(word)) {
                strangeSet.add(word);
                return;
            }

            if (ContainInBlack(word)) {
                return;
            }

            if (wordRankList.indexOf(word) > WordGrate && word.length() > 3) {
                strangeSet.add(word);
            }
        });

        return strangeSet;
    }

}
