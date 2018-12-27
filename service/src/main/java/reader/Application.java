package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reader.Services.BlackWhiteWordService;
import reader.Services.DocumentLoadingService;
import reader.Services.DocumentPresentService;
import reader.Services.LocalDictionaryService;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    BlackWhiteWordService blackWhiteWordService;

    @Autowired
    DocumentPresentService documentPresentService;

    @Autowired
    DocumentLoadingService documentLoadingService;

    @Override
    public void run(String... args) throws Exception {
        // load dictionary
        localDictionaryService.Load();

        // load blackWordList/whiteWordList
        blackWhiteWordService.Load();

        // load present document
        documentPresentService.Load();

        // we need to load the infrastructure service before load resource
        documentLoadingService.LoadAsync();
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncPool-");
        executor.initialize();
        return executor;
    }
}
