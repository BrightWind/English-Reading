package reader.Services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reader.Model.WordExplain;
import reader.Model.YouDao.YDResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Service
public class CloudDictionaryService {
    //private static final Logger logger = LoggerFactory.getLogger(GitHubLookupService.class);

    private final RestTemplate restTemplate;
    private final String YouDaoUrlTemple = "http://dict.youdao.com/jsonapi?xmlVersion=5.1&client=&q=%s&dicts=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=5g&abtest=&jsonversion=2";

    public CloudDictionaryService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /*
    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        logger.info("Looking up " + user);
        String url = String.format("https://api.github.com/users/%s", user);
        User results = restTemplate.getForObject(url, User.class);
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(results);
    }
    */

    public WordExplain QueryWord(String word) {
        try {
            String url = String.format(YouDaoUrlTemple, word);
            YDResult ydResult = restTemplate.getForObject(url, YDResult.class);
            if (ydResult != null)
            {
                WordExplain explain = new WordExplain();
                explain.word = word;
                explain.ukphone = ydResult.ec.word.get(0).ukphone;
                explain.ukspeech = ydResult.ec.word.get(0).ukspeech;
                explain.usphone = ydResult.ec.word.get(0).usphone;
                explain.usspeech = ydResult.ec.word.get(0).usspeech;
                ydResult.ec.word.get(0).trs.forEach(trs -> {
                    trs.tr.forEach(item -> explain.explain_list.add(item.l.i.get(0)));
                });
                return explain;
            }
        }
        catch (Exception ex) {}
        finally {
        }

        return null;
    }

    @Async
    public CompletableFuture<WordExplain> QueryWordAsync(String word) throws InterruptedException {
        WordExplain wordExplain = QueryWord(word);
        return CompletableFuture.completedFuture(wordExplain);
    }
}
