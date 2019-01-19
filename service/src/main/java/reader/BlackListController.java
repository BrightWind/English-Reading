package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reader.Helper.StringHelper;
import reader.Model.DocumentProfile;
import reader.Model.WhiteListParam;
import reader.Services.WhiteListService;
import reader.Services.ResourceLoadingService;
import reader.Services.DocumentInventoryService;

import java.util.*;
@CrossOrigin(value = "*")
@RestController
public class BlackListController {
    @Autowired
    WhiteListService blackWhiteWordService;

    @Autowired
    DocumentInventoryService documentPresentService;

    @Autowired
    ResourceLoadingService documentLoadingService;

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
        blackWhiteWordService.AddToWhiteList(word);
    }

    @RequestMapping(value = "list/black/add")
    public void AddBlack(String word) {
        blackWhiteWordService.AddToBlackList(word);
    }

    @RequestMapping(value = "list/white/list/add")
    public void AddListToWhite(@RequestBody WhiteListParam whiteListParam) {
        DocumentProfile documentProfile = documentPresentService.Get(whiteListParam.doc_id);
        if (documentProfile == null) {
            return;
        }

        Set<String> white_set = new HashSet<>();
        Set<String> black_set = new HashSet<>();

        //content to word set
        Set<String> word_set = StringHelper.SplitToWords(documentProfile.contentLines);

        //get white list
        whiteListParam.white_list.forEach(word -> {
            word = StringHelper.trim(word, "");
            white_set.add(word.toLowerCase());
        });

        //get black list
        word_set.forEach(word->{
            if (!white_set.contains(word)) {
                black_set.add(word);
            }
        });

        //update white and black list
        //blackWhiteWordService.AddListToWhite(white_set);
        blackWhiteWordService.AddListToBlack(black_set);

        //save document strange list
        documentPresentService.SaveStrangeWords(documentProfile, white_set);
    }

    @RequestMapping(value = "list/black/list/add")
    public void AddListToBlack(Set<String> list) {
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
