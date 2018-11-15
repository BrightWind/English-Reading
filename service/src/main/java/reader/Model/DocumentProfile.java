package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

public class DocumentProfile {
    @Id
    public String id;
    public String fileName;
    public List<String> contentLines;
    public String content;
    public Set<String> strangeWords;
    public long rPosition = 0;
}
