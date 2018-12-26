package reader.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Component
public class StrangeWordLevelDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<StrangeWordLevel> Get() {
        return mongoTemplate.findAll(StrangeWordLevel.class);
    }

    public void UpdateLowLevel(String id, int level) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                update("LowLevel", level),
                StrangeWordLevel.class);
    }

    public void UpdateHighLevel(String id, int level) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                update("HighLevel", level),
                StrangeWordLevel.class);
    }

    public void Add(StrangeWordLevel setting) {
        mongoTemplate.insert(setting);
    }
}
