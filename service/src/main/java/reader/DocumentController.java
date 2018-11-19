package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reader.Model.*;
import reader.Services.ClouldDictionaryService;
import reader.Services.LocalDictionaryService;
import reader.Services.WordBlackListService;


import java.util.ArrayList;
import java.util.List;

@CrossOrigin(value = "*")
@RestController
public class DocumentController {

    @Autowired
    TextLoader staticResource;

    @Autowired
    DocumentProfileDao documentProfileDao;

    @Autowired
    WordExplanDao wordExplanDao;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/document/list/get", method = RequestMethod.GET)
    public List<BriefDocumentProfile> GetDocumentList()
    {
        List<BriefDocumentProfile> list = new ArrayList<>();
        for (Object profile: staticResource.resources.values()) {
            BriefDocumentProfile briefDocumentProfile = new BriefDocumentProfile();
            briefDocumentProfile.id = ((DocumentProfile)profile).id;
            briefDocumentProfile.fileName = ((DocumentProfile)profile).fileName;
            list.add(briefDocumentProfile);
        }
        return list;
    }

    @Autowired
    ClouldDictionaryService clouldDictionaryService;

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    WordBlackListService wordBlackListService;

    @Autowired
    TextLoader textLoader;

    @RequestMapping(value = "/document/resource/reload", method = RequestMethod.GET)
    public void ReloadResource()
    {

    }

    @RequestMapping(value = "/document/get", method = RequestMethod.GET)
    public DocumentProfile GetDocument(String id)
    {
        if (staticResource.resources.containsKey(id))
        {
            DocumentProfile documentProfile = (DocumentProfile)staticResource.resources.get(id);
            List<String> tempList = new ArrayList<>();
            for (String word: documentProfile.strangeWords) {
                if (wordBlackListService.Contain(word)) {
                    tempList.add(word);
                    documentProfileDao.DeleteWord(documentProfile.id, word);
                }
            }

            for (String word: tempList) {
                documentProfile.strangeWords.remove(word);
            }

            return documentProfile;
        }
        else
        {
            return null;
        }
    }

    @RequestMapping(value = "/document/explain/get", method = RequestMethod.GET)
    public List<WordExplain> GetWordExplain(String doc_id)
    {
        List<WordExplain> wordExplainList = new ArrayList<>();
        if (staticResource.resources.containsKey(doc_id))
        {
            DocumentProfile profile = (DocumentProfile) staticResource.resources.get(doc_id);
            for (String word: profile.strangeWords) {
              WordExplain wordExplain = localDictionaryService.Get(word);
              if (wordExplain != null) {
                  wordExplainList.add(wordExplain);
              }
            }

            return wordExplainList;
        }
        else
        {
            return null;
        }
    }

    @RequestMapping(value = "/document/position")
    public boolean SetPosition(@RequestParam String doc_id, @RequestParam int index)
    {
        if (staticResource.resources.containsKey(doc_id))
        {
            DocumentProfile rs = (DocumentProfile)staticResource.resources.get(doc_id);
            if (rs != null)
            {
                documentProfileDao.UpdateIndex(doc_id, index);
                rs.rPosition = index;
            }
        }

        return false;
    }

    @RequestMapping(value = "document/strange/word/add")
    public void AddStrangeWord(@RequestParam String doc_id, @RequestParam String word)
    {
        String  tword = word.toLowerCase();
        if (!localDictionaryService.Find(tword)) {
            try {
                clouldDictionaryService.QueryWord(tword).thenAccept(wordExplain -> {
                    if (wordExplain != null)
                    {
                        localDictionaryService.Add(wordExplain);
                        documentProfileDao.AddWord(doc_id, tword);
                        wordExplanDao.Save(wordExplain);
                        DocumentProfile rs = (DocumentProfile)staticResource.resources.get(doc_id);
                        if (rs != null) rs.strangeWords.add(tword);
                    }
                });
            }
            catch (Exception ex) {};
        } else {
            documentProfileDao.AddWord(doc_id, tword);
            DocumentProfile rs = (DocumentProfile)staticResource.resources.get(doc_id);
            if (rs != null) rs.strangeWords.add(tword);
        }

    }

    @RequestMapping(value = "document/strange/word/delete")
    public void DeleteStrangeWord(@RequestParam String doc_id, @RequestParam String word)
    {
        DocumentProfile rs = (DocumentProfile)staticResource.resources.get(doc_id);
        if (rs != null)
        {
            rs.strangeWords.remove(word);
            wordBlackListService.Add(word);
            documentProfileDao.DeleteWord(doc_id, word);
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
