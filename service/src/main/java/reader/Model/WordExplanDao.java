package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WordExplanDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<WordExplain> Get() {
        return mongoTemplate.findAll(WordExplain.class);
    }

    public void Save(WordExplain wordExplain) {
        mongoTemplate.insert(wordExplain);
    }
}
