package reader.Services.DocLoadService;

import reader.Model.Document;

import java.util.List;

public class MongoDocLoader extends IDocLoader {
    public MongoDocLoader(ILoaderObserver iLoaderObserver) {
        super(iLoaderObserver);
    }

    @Override
    public void Load(String url){
        List<Document> documentList = documentDao.Get();
        if (documentList != null) {
            documentList.forEach(document -> {
                if (mLoaderObserver != null) mLoaderObserver.OnDocLoaded(document);
            });
        }
    }
}
