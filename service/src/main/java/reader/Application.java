package reader;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;
import reader.Model.WordExplain;
import reader.Model.WordExplanDao;

import java.util.List;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //@Bean
    //public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    //    return args -> {
//
    //        System.out.println("Let's inspect the beans provided by Spring Boot:");
//
    //        String[] beanNames = ctx.getBeanDefinitionNames();
    //        Arrays.sort(beanNames);
    //        for (String beanName : beanNames) {
    //            System.out.println(beanName);
    //        }
//
    //    };
    //}

    @Autowired
    public DocumentProfileDao documentProfileDao;

    @Autowired
    public WordExplanDao wordExplanDao;

    @Override
    public void run(String... args) throws Exception {
        //repository.deleteAll();

        /*
        WordExplain word = new WordExplain();
        word.word = "test";
        word.explain.add("it is a test");
        wordExplanDao.Save(word);
        List<WordExplain> wordExplainList = wordExplanDao.Get();

        DocumentProfile documentProfile = new DocumentProfile();
        documentProfile.content = "test";
        documentProfile.contentLines.add("this is a test");
        documentProfile.strangeWords.add("doc");
        documentProfileDao.Save(documentProfile);
        List<DocumentProfile> documentProfileList = documentProfileDao.Get();

        DocumentProfile documentProfile1 = documentProfileList.get(0);
        DocumentProfile documentProfile2 = documentProfileDao.Get(documentProfile1.id);
        documentProfileDao.UpdateIndex(documentProfile1.id, 100);
        documentProfileDao.AddWord(documentProfile1.id, "test2");
        documentProfile1 = documentProfileDao.Get(documentProfile1.id);
        */

        /*
        // save a couple of customers
        repository.save(new Customer("Alice", "Smith", "test"));
        repository.save(new Customer("Bob", "Smith", "test"));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Customer customer : repository.findAll()) {
            System.out.println(customer);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            System.out.println(customer);
        }

        System.out.println("Customers found with findByMiddleName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findByMiddleName("test")) {
            System.out.println(customer);
        }
        */
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }
}
