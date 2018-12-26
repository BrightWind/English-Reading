package reader.Services.DocLoadService;

import reader.Model.Document;

public interface ILoaderObserver {
    void OnDocLoaded(Document document);
}
