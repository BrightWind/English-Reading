package reader;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@RestController
public class ResourceController {
    @RequestMapping(value = "/res/load", method = RequestMethod.GET)
    public boolean Load(String url) {
        return false;
    }
}
