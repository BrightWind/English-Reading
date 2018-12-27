package reader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
    @RequestMapping(value = "/document/explain/get", method = RequestMethod.GET)
    public boolean Load(String url) {
        return false;
    }
}
