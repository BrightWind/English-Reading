package reader.Services.DocLoadService;

import reader.Model.DocumentDao;

public abstract class IDocLoader {
    protected ILoaderObserver mLoaderObserver;

    protected DocumentDao documentDao;

    public IDocLoader(ILoaderObserver iLoaderObserver) {
        mLoaderObserver = iLoaderObserver;
    }
    public abstract void Load(String url);

    public static IDocLoader Create(int type, ILoaderObserver iLoaderObserver) {
        IDocLoader iDocLoader = null;
        switch (type) {
            case 0:
                iDocLoader = new LocalDocLoader(iLoaderObserver);
                break;
            case 1:
                iDocLoader = new WebDocLoader(iLoaderObserver);
                break;
            case 2:
                iDocLoader = new MongoDocLoader(iLoaderObserver);
                break;
            default:
                break;
        }

        return iDocLoader;
    }
}
