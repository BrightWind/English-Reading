package reader.Model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ResourceProfileRepository extends MongoRepository<ResourceProfile, String> {
    public ResourceProfile findById();
    public ResourceProfile findByFileName(String name);
}
