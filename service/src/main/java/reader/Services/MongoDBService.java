package reader.Services;

import org.springframework.stereotype.Service;
import reader.Model.WordExplain;

@Service
public class MongoDBService {

    public boolean AddToSet(String setId, String word) {
        return false;
    }

    public boolean AddToSet(String setId, WordExplain wordExplain) {
        return false;
    }

    public boolean updateDocPos(String doc, int idx) {
        return false;
    }

}
