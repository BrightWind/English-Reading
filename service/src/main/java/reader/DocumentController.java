package reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reader.Helper.StringHelper;
import reader.Model.ResourceProfile;
import reader.Services.ClouldDictionaryService;
import reader.Services.LocalDictionaryService;
import reader.Services.MongoDBService;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public Set GetDocumentList()
    {
        return staticResource.resources.keySet();
    }

    @Autowired
    ClouldDictionaryService clouldDictionaryService;

    @Autowired
    LocalDictionaryService localDictionaryService;

    @Autowired
    MongoDBService dbService;

    @RequestMapping(value = "/document/get", method = RequestMethod.GET)
    public ResourceProfile GetDocument(String id)
    {
        if (staticResource.resources.containsKey(id))
        {
            return (ResourceProfile)staticResource.resources.get(id);
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
            ResourceProfile rs = (ResourceProfile)staticResource.resources.get(doc_id);
            if (rs != null)
            {
                if (dbService.updateDocPos(doc_id, index)) {
                    rs.readingOffset = index;
                }
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

                            String docWordSet = "";
                            String globalWordSet = "";

                            if (dbService.AddToSet(docWordSet, word)) {
                                //add to document set
                            }

                            if (dbService.AddToSet(globalWordSet, wordExplain))
                            {
                                //add to global set
                            }

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
