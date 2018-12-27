package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class LocalDictionaryDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public boolean Save(LocalDictionary dic)
    {
        mongoTemplate.insert(dic);
        return false;
    }

    public LocalDictionary Get(String id) {
        return mongoTemplate.findById(id, LocalDictionary.class);
    }

    public List<LocalDictionary> Get() {
        return mongoTemplate.findAll(LocalDictionary.class);
    }

    public void AddWord(String id, WordExplain wordExplain) {
        Map<String,Object> m1=new HashMap<>();
        m1.put(wordExplain.word, wordExplain);

        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().push("container", m1),
                LocalDictionary.class);
    }

    public void DropCollection() {
        mongoTemplate.dropCollection(LocalDictionary.class);
    }
}
