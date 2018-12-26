package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

public class WordBlackList {
    @Id
    public String id;
    public Set<String> blackList = new HashSet<>();
    public Set<String> whiteList = new HashSet<>();

}
