package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import reader.Model.Person;
import reader.Model.WordExplain;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Service
public class MongoDBService {
    @Autowired
    MongoTemplate mongoOps;


    public void test(String[] args) {
        Person p = new Person("Joe", 34);

        // Insert is used to initially store the object into the database.
        mongoOps.insert(p);

        // Find
        p = mongoOps.findById(p.getId(), Person.class);

        // Update
        mongoOps.updateFirst(query(where("name").is("Joe")), update("age", 35), Person.class);
        p = mongoOps.findOne(query(where("name").is("Joe")), Person.class);

        // Delete
        mongoOps.remove(p);

        // Check that deletion worked
        List<Person> people =  mongoOps.findAll(Person.class);

        mongoOps.dropCollection(Person.class);
    }

    public boolean AddToSet(String obj_id, String word) {
        //mongoOps.upsert(query(where("ssn").is(1111).and("firstName").is("Joe").and("Fraizer").is("Update")), update("address", addr), Person.class);
        mongoOps.upsert(query(where("obj_id").is(obj_id)), update("address", word), Person.class);
        return false;
    }

    public boolean AddToSet(String setId, WordExplain wordExplain) {

        return false;
    }

    public boolean updateDocPos(String doc, int idx) {
        return false;
    }

}
