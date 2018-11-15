package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class WordExplain {
    @Id
    public String id;
    public String word;
    public String ukphone;
    public String ukspeech;
    public String usphone;
    public String usspeech;
    public List<String> explain = new ArrayList<>();
}
