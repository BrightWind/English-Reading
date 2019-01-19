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
    public String url;
    public String category;
    public List<String> contentLines = new ArrayList<>();
    public Set<String> strangeWords = new HashSet<>();
    public Set<String> word_list = new HashSet<>();
    public long rPosition = 0;

    public DocumentProfile OutputClone() {
        DocumentProfile profile = new DocumentProfile();
        profile.id = this.id;
        profile.fileName = this.fileName;
        profile.url = this.url;
        profile.category = this.category;
        profile.contentLines = this.contentLines;
        profile.strangeWords = this.strangeWords;
        profile.rPosition = this.rPosition;
        return profile;
    }
}
