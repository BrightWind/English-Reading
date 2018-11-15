package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reader.Model.DocumentProfile;
import reader.Model.DocumentProfileDao;
import reader.Model.WordExplanDao;
import reader.Services.ClouldDictionaryService;
import reader.Services.LocalDictionaryService;


import java.util.List;
import java.util.Set;

@CrossOrigin(value = "*")
@RestController
public class DocumentController {

    @Autowired
    StaticResource staticResource;

    @Autowired
    DocumentProfileDao documentProfileDao;

    @Autowired
    WordExplanDao wordExplanDao;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/document/list/get", method = RequestMethod.GET)
    public Set GetDocumentList()
    {
        return staticResource.resources.keySet();
    }

    @Autowired
    ClouldDictionaryService clouldDictionaryService;

    @Autowired
    LocalDictionaryService localDictionaryService;

    @RequestMapping(value = "/document/get", method = RequestMethod.GET)
    public DocumentProfile GetDocument(String id)
    {
        if (staticResource.resources.containsKey(id))
        {
            return (DocumentProfile)staticResource.resources.get(id);
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

    @RequestMapping(value = "document/strangeword/add")
    public void AddStrangeWord(@RequestParam String doc_id, @RequestParam List<String> word_list)
    {
        for (String word: word_list) {
            if (!localDictionaryService.Find(word))
                try {
                    clouldDictionaryService.QueryWord(word).thenAccept(wordExplain -> {
                        if (wordExplain != null)
                        {
                            localDictionaryService.Add(wordExplain);
                            documentProfileDao.AddWord(doc_id, word);
                            wordExplanDao.Save(wordExplain);
                        }
                    });
                }
                catch (Exception ex) {};
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
