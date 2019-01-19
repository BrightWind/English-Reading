package reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reader.Helper.StringHelper;
import reader.Model.*;
import reader.Services.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(value = "*")
@RestController
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(WordQueryingService.class);

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    CloudDictionaryService cloudDictionaryService;

    @Autowired
    DocumentInventoryService documentPresentService;

    @Autowired
    WhiteListService whiteWordService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/document/list/get", method = RequestMethod.GET)
    public List<BriefDocumentProfile> GetDocumentList()
    {
        List<BriefDocumentProfile> list = new ArrayList<>();
        List<DocumentProfile> documentProfileList = documentPresentService.Get();
        for (Object profile: documentProfileList) {
            BriefDocumentProfile briefDocumentProfile = new BriefDocumentProfile();
            briefDocumentProfile.id = ((DocumentProfile)profile).id;
            briefDocumentProfile.fileName = ((DocumentProfile)profile).fileName;
            list.add(briefDocumentProfile);
        }
        return list;
    }

    @RequestMapping(value = "/document/resource/reload", method = RequestMethod.GET)
    public void ReloadResource()
    {

    }

    @RequestMapping(value = "/document/get", method = RequestMethod.GET)
    public DocumentProfile GetDocument(String id)
    {
        DocumentProfile documentProfile = documentPresentService.Get(id);
        if (documentProfile != null) {
            documentProfile.strangeWords = whiteWordService.CaptureStrangeWord(documentProfile.word_list);
            documentProfile = documentProfile.OutputClone();
        }

        return documentProfile;
    }

    @RequestMapping(value = "/document/explain/get", method = RequestMethod.GET)
    public List<WordExplain> GetWordExplain(String doc_id)
    {
        DocumentProfile profile = documentPresentService.Get(doc_id);
        if (profile == null) return null;

        List<WordExplain> wordExplainList = new ArrayList<>();
        for (String word: profile.strangeWords) {
            WordExplain wordExplain = localDictionaryService.Get(word);
            if (wordExplain != null) {
                wordExplainList.add(wordExplain);
            }
        }

        logger.info("Get wordExplainList size: %d, %d", profile.strangeWords.size(), wordExplainList.size());
        return wordExplainList;
    }

    @RequestMapping(value = "/document/position")
    public boolean SetPosition(@RequestParam String doc_id, @RequestParam int index)
    {
        DocumentProfile rs = documentPresentService.Get(doc_id);
        if (rs != null)
        {
            documentPresentService.UpdateIndex(rs, index);
        }

        return true;
    }

    @RequestMapping(value = "document/strange/word/add")
    public WordExplain AddStrangeWord(@RequestParam String doc_id, @RequestParam String word)
    {
        String  tword = word.toLowerCase();
        tword = StringHelper.trim(tword, "");
        DocumentProfile rs = documentPresentService.Get(doc_id);
        if (!localDictionaryService.Contain(tword)) {
            try {
                CompletableFuture<WordExplain> future = cloudDictionaryService.QueryWordAsync(tword);
                WordExplain wordExplain = future.get();
                if (wordExplain == null) {
                    return null;
                }

                localDictionaryService.Add(wordExplain);
                if (rs != null) documentPresentService.AddWord(rs, tword);

                return wordExplain;
            }
            catch (Exception ex) {
                return null;
            }
        } else {
            if (rs != null) documentPresentService.AddWord(rs, tword);
            return localDictionaryService.Get(tword);
        }
    }

    @RequestMapping(value = "document/strange/word/delete")
    public void DeleteStrangeWord(@RequestParam String doc_id, @RequestParam String word)
    {
        DocumentProfile rs = documentPresentService.Get(doc_id);
        if (rs != null)
        {
            documentPresentService.RemoveWord(rs, word);
        }
    }



    private  void Draft () {

        /*
        SA.getDocumentList()
        [
            {
              id
              name
              category
            }

        ]

        SA.getDocument(id)
        {
           id,
           name,
           category,
           readingPosition,
           content {},
           StrangeWord: [
             {
                word,
                speak
                explain : []
             }
           ],
        }

        sa.addStrangeWord()
        sa.setReadingPosition()


        bean
        DocumentProfile
        WordExplain
        */
    }
}
