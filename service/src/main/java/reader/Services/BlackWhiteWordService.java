package reader.Services;

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

    public List<String> CaptureValidLines(List<String> lines) {
        List<String> newLines = new ArrayList<>();

        String pattern = "\\d\\d:\\d\\d:\\d\\d";
        Pattern regex = Pattern.compile(pattern);
        lines.forEach(line -> {
            line.trim();

            if (line.indexOf("{\\") != -1)
            {
                return;
            }

            //it is too small, should no be a word
            if (line.length() < 5){
                return;
            }

            //it should be time string
            Matcher result = regex.matcher(line);
            if (result.find() && result.find()) //at least match twice to ensure it was not a content line
            {
                return;
            }

            newLines.add(line);
        });
        return newLines;
    }

    public Set<String> CaptureWords(List<String> lines) {
        Set<String> word_set = new HashSet<>();

        for (String line : lines) {
            //it should be chinese
            if (ChineseChecker.hasChinese(line))
            {
                continue;
            }

            //collect long word
            String words[] = line.split(" ");
            ArrayDeque<String> wordQueue = new ArrayDeque(Arrays.asList(words));
            while (wordQueue.size() > 0) {
                String word = wordQueue.pop();
                String trimWord = StringHelper.trim(word, ",.?!()-\"").toLowerCase();

                if (trimWord.isEmpty()) {
                    continue;
                }

                if (trimWord.contains("'s")
                        ||trimWord.contains("'t")
                        ||trimWord.contains("'v")
                        ||trimWord.contains("'d")
                        ||trimWord.contains("'re")) {
                    continue;
                }

//                if (trimWord.indexOf(".") != -1
//                    || trimWord.indexOf("-") != -1
//                    || trimWord.indexOf("/") != -1) {
//                    continue;
//                }

                trimWord.toLowerCase();

                word_set.add(trimWord);
            }
        }

        return word_set;
    }

    public Set<String> CaptureStrangeWord(Set<String> words) {
        Set<String> strangeSet = new HashSet<>();
        words.forEach(word -> {
            if (ContainInWhite(word)) {
                strangeSet.add(word);
                return;
            }

            if (ContainInBlack(word)) {
                return;
            }

            if (word.length() < 7) {
                AddToBlack(word);
            }
            else {
                AddToWhite(word);
                strangeSet.add(word);
            }
        });

        return strangeSet;
    }

}
