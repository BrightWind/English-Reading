package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class WordBlackListDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<WordBlackList> Get() {
        return mongoTemplate.findAll(WordBlackList.class);
    }


    public boolean Save(WordBlackList wordBlackList)
    {
        mongoTemplate.insert(wordBlackList);
        return true;
    }

    public void AddWord(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("blackList", word),
                WordBlackList.class);
    }

    public void DeleteWord(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().pull("blackList", word),
                WordBlackList.class);
    }

}
