package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


@Component
public class DocumentDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public boolean Save(Document doc)
    {
        mongoTemplate.insert(doc);
        return false;
    }

    public Document Get(String id) {
        return mongoTemplate.findById(id, Document.class);
    }

    public Document GetByURL(String url) {
        return mongoTemplate.findOne(query(where("url").is(url)), Document.class);
    }

    public List<Document> Get() {
        return mongoTemplate.findAll(Document.class);
    }

    public void DropCollection() {
        mongoTemplate.dropCollection(Document.class);
    }
}
