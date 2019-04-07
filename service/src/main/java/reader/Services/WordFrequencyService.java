package reader.Services;

import org.springframework.stereotype.Service;

import java.util.*;


class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

@Service
public class WordFrequencyService {
    public HashMap<String, Integer> WordFrequency = new HashMap<String, Integer>();

    public List<String> SortMapToList(HashMap<String, Integer> map) {
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<>(bvc);
        sorted_map.putAll(map);
        return new ArrayList(sorted_map.keySet());
    }

    public void AddWord(String word) {
        synchronized (WordFrequency) {
            if (WordFrequency.containsKey(word)) {
                WordFrequency.put(word, WordFrequency.get(word) + 1);
            } else {
                WordFrequency.put(word, 1);
            }
        }
    }

    public void AddWord(Set<String> word_list) {
        synchronized (WordFrequency) {
            word_list.forEach(word -> {
                if (WordFrequency.containsKey(word)) {
                    WordFrequency.put(word, WordFrequency.get(word) + 1);
                } else {
                    WordFrequency.put(word, 1);
                }
            });
        }
    }

    public List<String> GenerateWordRankList() {
        synchronized (WordFrequency) {
            return SortMapToList(WordFrequency);
        }
    }

    public HashMap<String, Integer> GetRank() {
        return WordFrequency;
    }
}
