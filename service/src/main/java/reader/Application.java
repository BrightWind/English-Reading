package reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reader.Helper.StringHelper;
import reader.Services.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@Slf4j
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    CloudDictionaryService cloudDictionaryService;

    @Autowired
    WhiteListService whiteListService;

    @Autowired
    DocumentInventoryService documentInventoryService;

    @Autowired
    ResourceLoadingService resourceLoadingService;

    @Autowired
    WordQueryingService wordQueryingService;

    @Autowired
    WordFrequencyService wordFrequencyService;

    @Override
    public void run(String... args) throws Exception {
        // load dictionary
        localDictionaryService.Load();
        log.info("----step1 localDictionaryService.Load()");

        // load blackWordList/whiteWordList
        whiteListService.Load();
        log.info("----step2 whiteListService.Load()");

        // load present document
        documentInventoryService.Load(documentProfiles -> {
            if (documentProfiles == null) return;

            Set<String> word_set = new HashSet<>();
            documentProfiles.forEach(documentProfile -> {
                List<String> contents = StringHelper.CaptureEnglishSentences(documentProfile.contentLines);
                documentProfile.word_list = StringHelper.SplitToWords(contents);
                wordFrequencyService.AddWord(documentProfile.word_list);
                log.info(String.format("----step3 documentInventoryService.Load():%s", documentProfile.id));
            });

            documentProfiles.forEach(documentProfile -> {
                documentProfile.strangeWords = whiteListService.CaptureStrangeWord(documentProfile.word_list);
                word_set.addAll(documentProfile.strangeWords);
            });

            CompletableFuture completableFuture = wordQueryingService.QueryWordAsync(word_set);
            completableFuture.thenAccept(param ->{
                // we need to load the infrastructure service before load resource
                resourceLoadingService.StartAsync();
            });
        });
        log.info("----step4 documentInventoryService.Load()");
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
