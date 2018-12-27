package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.HashMap;

public class LocalDictionary {
    @Id
    public String id;
    public HashMap<String, WordExplain> container = new HashMap<>();
}
