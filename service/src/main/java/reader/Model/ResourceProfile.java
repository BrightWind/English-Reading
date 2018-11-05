package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

public class ResourceProfile {
    @Id
    public String id;
    public String fileName;
    public List<String> contentLines;
    public String content;
    public Set<String> strangeWords;
    public long readingOffset = 0;
}
