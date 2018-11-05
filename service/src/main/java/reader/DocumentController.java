package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

@CrossOrigin(value = "*")
@RestController
public class DocumentController {

    @Autowired
    StaticResource staticResource;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/document/list/get", method = RequestMethod.GET)
    public List<String> GetDocumentList()
    {
        List<String> listContent  = new ArrayList<>();
        for (Object content: staticResource.resources.keySet().toArray()) {
            String line = String.format("/document/list/get?id=%s", content.toString(), content.toString());
            listContent.add(line);
        }
        return listContent;
    }

    @RequestMapping(value = "/document/get", method = RequestMethod.GET)
    public String GetDocument(String id)
    {
        if (staticResource.resources.containsKey(id))
        {
            return "<pre>" + staticResource.resources.get(id).toString() + "</pre>";
        }
        else
        {
            return "Document not found";
        }
    }

    String YouDaoUrl = "http://dict.youdao.com/jsonapi?xmlVersion=5.1&client=&q=account&dicts=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=5g&abtest=&jsonversion=2";

    @RequestMapping(value = "/youdao/get", method = RequestMethod.GET)
    public String test()
    {
        RestTemplate restTemplate = new RestTemplate();
        String quote = restTemplate.getForObject(YouDaoUrl, String.class);
         return null;
    }
}
