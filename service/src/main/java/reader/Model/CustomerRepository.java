package reader.Model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

//Auto Implement by java and actually working by MongoTemplate

@Component
public interface CustomerRepository extends MongoRepository<Customer, String> {
    public Customer findByFirstName(String firstName);
    public List<Customer> findByLastName(String lastName);
    public List<Customer> findByMiddleName(String lastName);
}
