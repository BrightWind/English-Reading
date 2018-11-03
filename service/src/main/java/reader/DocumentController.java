package reader;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@RestController
public class DocumentController {

    @Autowired
    StaticResource staticResource;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/document/list/get", method = RequestMethod.GET)
    public Set<String> GetDocumentList()
    {
        return staticResource.resources.keySet();
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
}
