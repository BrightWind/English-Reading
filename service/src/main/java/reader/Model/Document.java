package reader.Model;

import org.springframework.data.annotation.Id;

public class Document {
    @Id
    public String id;
    public String url;
    public String tag;
    public String content;
}
