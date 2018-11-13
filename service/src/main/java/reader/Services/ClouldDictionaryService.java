package reader.Services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reader.Model.YouDao.YDResult;

import java.util.concurrent.CompletableFuture;

@Service
public class ClouldDictionaryService {
    //private static final Logger logger = LoggerFactory.getLogger(GitHubLookupService.class);

    private final RestTemplate restTemplate;
    private final String YouDaoUrlTemple = "http://dict.youdao.com/jsonapi?xmlVersion=5.1&client=&q=%s&dicts=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=5g&abtest=&jsonversion=2";

    public ClouldDictionaryService(RestTemplateBuilder restTemplateBuilder) {
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

    @Async
    public CompletableFuture<YDResult> QueryWord(String word) throws InterruptedException {
        String url = String.format(YouDaoUrlTemple, word);
        YDResult results = restTemplate.getForObject(url, YDResult.class);
        return CompletableFuture.completedFuture(results);
    }
}
