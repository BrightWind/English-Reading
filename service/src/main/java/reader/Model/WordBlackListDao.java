package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class WordBlackListDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<WordBlackList> Get() {
        return mongoTemplate.findAll(WordBlackList.class);
    }

    public boolean Save(WordBlackList wordBlackList) {
        mongoTemplate.insert(wordBlackList);
        return true;
    }

    public void AddToWhite(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("whiteList", word),
                WordBlackList.class);
    }

    public void AddToBlack(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("blackList", word),
                WordBlackList.class);
    }

    public void AddListToWhite(String id, Set<String> listWord) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("whiteList").each(listWord),
                WordBlackList.class);
    }

    public void AddListToBlack(String id, Set<String> listWord) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("blackList").each(listWord),
                WordBlackList.class);
    }

    public void DeleteFromWhite(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().pull("whiteList", word),
                WordBlackList.class);
    }

    public void DeleteFromBlack(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().pull("blackList", word),
                WordBlackList.class);
    }

}
