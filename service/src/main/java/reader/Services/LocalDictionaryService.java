package reader.Services;

import org.springframework.stereotype.Service;
import reader.Model.WordExplain;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LocalDictionaryService {
    public LocalDictionaryService() {

    }

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private HashMap<String, WordExplain> container = new HashMap<>();

    public WordExplain Get(String word)
    {
        rwLock.readLock().lock();
        try{
            return  container.containsKey(word)? container.get(word): null;
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
            return  container.containsKey(word);
        }catch(Exception ex){

        }finally{
            rwLock.readLock().unlock();   //释放锁
        }
        return false;
    }

    public void Add(WordExplain wordExplain) {
        rwLock.writeLock().lock();
        try {
            container.put(wordExplain.word, wordExplain);
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
                    container.put(wordExplain.word, wordExplain);
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
            container.remove(wordExplain.word);
        } catch (Exception ex) {

        } finally {
            rwLock.writeLock().unlock();   //释放锁
        }
    }
}
