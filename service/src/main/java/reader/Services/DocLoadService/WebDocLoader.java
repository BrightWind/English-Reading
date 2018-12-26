package reader.Services.DocLoadService;

import reader.Model.Document;

public class WebDocLoader extends IDocLoader {
    public WebDocLoader(ILoaderObserver iLoaderObserver) {
        super(iLoaderObserver);
    }

    @Override
    public void Load(String url){
        Document document = new Document();
        documentDao.Save(document);
        if (mLoaderObserver != null) mLoaderObserver.OnDocLoaded(document);
    }
}
