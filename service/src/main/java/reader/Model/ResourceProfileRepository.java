package reader.Model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ResourceProfileRepository extends MongoRepository<ResourceProfile, String> {
    ResourceProfile findById();
    ResourceProfile findByFileName(String name);
}
