package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Model.LocalDictionary;
import reader.Model.LocalDictionaryDao;
import reader.Model.WordExplain;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LocalDictionaryService {
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private LocalDictionary dictionary;

    @Autowired
    private LocalDictionaryDao localDictionaryDao;


    public LocalDictionaryService() {

    }

    public void Load() {
        List<LocalDictionary> localDictionaries = localDictionaryDao.Get();
        if (localDictionaries == null || localDictionaries.size() == 0) {
            dictionary = new LocalDictionary();
            localDictionaryDao.Save(dictionary);
        }
    }

    public WordExplain Get(String word)
    {
        rwLock.readLock().lock();
        try{
            return  dictionary.container.containsKey(word)? dictionary.container.get(word): null;
            //处理任务
        }catch(Exception ex){

        }finally{
            rwLock.readLock().unlock();   //释放锁
        }
        return null;
    }

    public boolean Find(String word)
    {
        rwLock.readLock().lock();
        try{
            return dictionary.container.containsKey(word);
        }catch(Exception ex){

        }finally{
            rwLock.readLock().unlock();   //释放锁
        }
        return false;
    }

    public void Add(WordExplain wordExplain) {
        rwLock.writeLock().lock();
        try {
            dictionary.container.put(wordExplain.word, wordExplain);
            localDictionaryDao.AddWord(wordExplain.word, wordExplain);
            //处理任务
        } catch (Exception ex) {

        } finally {
            rwLock.writeLock().unlock();   //释放锁
        }
    }

    public void Add(List<WordExplain> wordExplainList) {
        rwLock.writeLock().lock();
        try {
            for (WordExplain wordExplain : wordExplainList) {
                try {
                    dictionary.container.put(wordExplain.word, wordExplain);
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {

        } finally {
            rwLock.writeLock().unlock();   //释放锁
        }
    }

    public void remove(WordExplain wordExplain) {
        rwLock.writeLock().lock();
        try {
            dictionary.container.remove(wordExplain.word);
        } catch (Exception ex) {

        } finally {
            rwLock.writeLock().unlock();   //释放锁
        }
    }
}
