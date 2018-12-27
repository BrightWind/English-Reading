package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reader.Model.*;
import reader.Services.*;


import java.util.ArrayList;
import java.util.List;

@CrossOrigin(value = "*")
@RestController
public class DocumentController {
    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    CloudDictionaryService cloudDictionaryService;

    @Autowired
    DocumentPresentService documentPresentService;

    @Autowired
    BlackWhiteWordService whiteWordService;

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
            List<String> tempList = new ArrayList<>();
            for (String word: documentProfile.strangeWords) {
                if (!whiteWordService.ContainInBlack(word) && whiteWordService.ContainInBlack(word)) {
                    tempList.add(word);
                }
            }

            for (String word: tempList) {
                documentProfile.strangeWords.remove(word);
            }
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
    public void AddStrangeWord(@RequestParam String doc_id, @RequestParam String word)
    {
        String  tword = word.toLowerCase();
        DocumentProfile rs = documentPresentService.Get(doc_id);
        if (!localDictionaryService.Contain(tword)) {
            try {
                cloudDictionaryService.QueryWordAsync(tword).thenAccept(wordExplain -> {
                    if (wordExplain != null)
                    {
                        localDictionaryService.Add(wordExplain);
                        if (rs != null) documentPresentService.AddWord(rs, tword);
                    }
                });
            }
            catch (Exception ex) {};
        } else {

            if (rs != null) documentPresentService.AddWord(rs, tword);
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
