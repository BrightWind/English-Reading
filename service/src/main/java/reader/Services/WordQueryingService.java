package reader.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reader.Model.Document;
import reader.Model.WordExplain;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WordQueryingService {
    private static final Logger logger = LoggerFactory.getLogger(WordQueryingService.class);

    protected Queue<Document> task = new ArrayDeque<>(10);

    @Autowired
    CloudDictionaryService cloudDictionaryService;

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    WhiteListService whiteWordService;

    private String GetOrgPattern(String line) {
        String pattern = "（([a-zA-Z]+)的";
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private String GetOrgPatternFromList(List<String> explain_list) {
        for (String line : explain_list) {
            String org = GetOrgPattern(line);
            if (org != null) {
                return org;
            }
        }

        return null;
    }

    final Semaphore semaphore = new Semaphore(10);

    int count = 0;

    public void Increase() {
        synchronized (this) {
            count++;
            logger.info(String.format("semaphore count:%d, ++" ,count));
        }
    }
    public void Decrease() {
        synchronized (this) {
            count--;
            logger.info(String.format("semaphore count:%d,--", count));
        }
    }

    public void HandleQueryResult(String word, WordExplain wordExplain) {
        if (wordExplain == null) {
            whiteWordService.AddToBlackList(word);
            logger.info(String.format("failed to query:%s", word));
            return;
        }

        String originalPattern = GetOrgPatternFromList(wordExplain.explain_list);
        if (originalPattern == null) {
            localDictionaryService.Add(wordExplain);
            return;
        }

        originalPattern.toLowerCase();
        wordExplain = cloudDictionaryService.QueryWord(originalPattern);
        if (wordExplain == null) {
            whiteWordService.AddToBlackList(word);
            return;
        }

        localDictionaryService.Add(wordExplain);
        WordExplain variantPattern = (WordExplain)wordExplain.clone();
        variantPattern.word = word;
        localDictionaryService.Add(variantPattern);
    }

    @Async
    public CompletableFuture QueryWordAsync(Set<String> wordList) {
        logger.info(String.format("word set size:%d", wordList.size()));
        wordList.forEach(word -> {
            try {
                count++;

                semaphore.acquire();
                //logger.info(String.format("semaphore :word:%s, count:%d, semaphore:%d", word, count, semaphore.availablePermits()));
                if (localDictionaryService.Contain(word)) {
                    return;
                }

                cloudDictionaryService.QueryWordAsync(word).thenAccept(wordExplain -> {
                    HandleQueryResult(word, wordExplain);
                });
            } catch (Exception ex) {
            }
            finally {
                semaphore.release();
            }
        });

        return CompletableFuture.completedFuture(null);
    }

}
