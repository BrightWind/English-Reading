package reader;

import java.util.Arrays;

import org.springframework.data.mongodb.MongoDbFactory;
import reader.Model.Customer;
import reader.Model.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private CustomerRepository repository;

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


    @Override
    public void run(String... args) throws Exception {


        repository.deleteAll();

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


    }



}
