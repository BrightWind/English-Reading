package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentProfile {
    @Id
    public String id;
    public String fileName;
    public String category;
    public List<String> contentLines = new ArrayList<>();
    public Set<String> strangeWords = new HashSet<>();
    public long rPosition = 0;
}
