package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public DocumentProfile GetDocument(String id) {
        return mongoTemplate.findById(id, DocumentProfile.class);
    }

    public List<DocumentProfile> GetDocument() {
        return mongoTemplate.findAll(DocumentProfile.class);
    }

    public void UpdateIndex(String id, int position) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                update("rPosition", 35),
                DocumentProfile.class);
    }

    public void AddWord(String id, String word) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().addToSet("strangeWords", word),
                DocumentProfile.class);
    }
}
