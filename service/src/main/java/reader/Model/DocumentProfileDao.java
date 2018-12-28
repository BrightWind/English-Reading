package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Component
public class DocumentProfileDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public boolean Save(DocumentProfile doc)
    {
        mongoTemplate.insert(doc);
        return false;
    }

    public DocumentProfile Get(String id) {
        return mongoTemplate.findById(id, DocumentProfile.class);
    }

    public DocumentProfile GetByName(String fileName) {
        return mongoTemplate.findOne(query(where("fileName").is(fileName)), DocumentProfile.class);
    }

    public DocumentProfile GetByURL(String url) {
        return mongoTemplate.findOne(query(where("url").is(url)), DocumentProfile.class);
    }

    public List<DocumentProfile> Get() {
        return mongoTemplate.findAll(DocumentProfile.class);
    }

    public void UpdateIndex(String id, int position) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                update("rPosition", position),
                DocumentProfile.class);
    }

    public void AddWord(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("strangeWords", word),
                DocumentProfile.class);
    }

    public void SaveStrangeWord(String id, Set<String> word_set) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set("strangeWords", word_set),
                DocumentProfile.class);
    }


    public void DeleteWord(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().pull("strangeWords", word),
                DocumentProfile.class);
    }

    public void DropCollection() {
        mongoTemplate.dropCollection(DocumentProfile.class);
    }
}
