package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reader.Services.BlackWhiteWordService;

import java.util.List;

@RestController
public class BlackListController {
    @Autowired
    BlackWhiteWordService blackWhiteWordService;

    @RequestMapping(value = "list/white/contain")
    public boolean ContainInWhite(String word) {
        return blackWhiteWordService.ContainInWhite(word);
    }

    @RequestMapping(value = "list/black/contain")
    public boolean ContainInBlack(String word) {
        return blackWhiteWordService.ContainInBlack(word);
    }

    @RequestMapping(value = "list/white/add")
    public void AddToWhite(String word) {
        blackWhiteWordService.AddToWhite(word);
    }

    @RequestMapping(value = "list/black/add")
    public void AddBlack(String word) {
        blackWhiteWordService.AddToBlack(word);
    }

    @RequestMapping(value = "list/white/list/add")
    public void AddListToWhite(List<String> list) {
        blackWhiteWordService.AddListToWhite(list);
    }

    @RequestMapping(value = "list/black/list/add")
    public void AddListToBlack(List<String> list) {
        blackWhiteWordService.AddListToBlack(list);
    }

    @RequestMapping(value = "list/white/remove")
    public void RemoveFromWhite(String word) {
        blackWhiteWordService.RemoveFromWhite(word);
    }

    @RequestMapping(value = "list/black/remove")
    public void RemoveFromBlack(String word) {
        blackWhiteWordService.RemoveFromBlack(word);
    }

}
