package reader.Model;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class WordExplain implements Cloneable {
    @Id
    public String id;
    public String word;
    public String ukphone;
    public String ukspeech;
    public String usphone;
    public String usspeech;
    public List<String> explain_list = new ArrayList<>();

    @Override
     public Object clone() {
        WordExplain explain = null;
         try{
             explain = (WordExplain)super.clone();
         }catch(CloneNotSupportedException e) {
             e.printStackTrace();
         }
         return explain;
     }
}
