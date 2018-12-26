package reader.Model;

import org.springframework.data.annotation.Id;

public class StrangeWordLevel {
    @Id
    public String id;
    public int LowLevel;
    public int HighLevel;
}
