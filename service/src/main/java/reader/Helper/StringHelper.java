package reader.Helper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    public static String trim(String source, String beTrim) {
        if(source == null){
            return "";
        }
        source = source.trim(); // 循环去掉字符串首的beTrim字符
        if(source.isEmpty()){
            return "";
        }

        int len = source.length();
        int i = len - 1;
        for (; i > -1; i--) {
            if (source.charAt(i) >= 'A' && source.charAt(i) <= 'z') {
                break;
            }
        }
        if (i == -1) {
            return "";
        }

        int e = 0;
        for (; e < len; e++) {
            if (source.charAt(e) >= 'A' && source.charAt(e) <= 'z') {
                break;
            }
        }

        if (i == len - 1 && e == 0) {
            return  source;
        }

        return source.substring(e, i + 1);
    }

    public static List<String> CaptureEnglishSentences(List<String> lines) {
        List<String> newLines = new ArrayList<>();

        String pattern = "\\d\\d:\\d\\d:\\d\\d";
        Pattern regex = Pattern.compile(pattern);
        lines.forEach(line -> {
            line.trim();

            if (line.indexOf("{\\") != -1)
            {
                return;
            }

            //it is too small, should no be a word
            if (line.length() < 5){
                return;
            }

            //it should be time string
            Matcher result = regex.matcher(line);
            if (result.find() && result.find()) //at least match twice to ensure it was not a content line
            {
                return;
            }

            newLines.add(line);
        });
        return newLines;
    }

    public static Set<String> SplitToWords(List<String> lines) {
        Set<String> word_set = new HashSet<>();

        for (String line : lines) {
            //it should be chinese
            if (ChineseChecker.hasChinese(line))
            {
                continue;
            }

            //collect long word
            String words[] = line.split(" ");
            ArrayDeque<String> wordQueue = new ArrayDeque(Arrays.asList(words));
            while (wordQueue.size() > 0) {
                String word = wordQueue.pop();
                String trimWord = StringHelper.trim(word, ",.?!()-\"").toLowerCase();

                if (trimWord.isEmpty()) {
                    continue;
                }

                if (trimWord.contains("'")
                        ||trimWord.contains("'s")
                        ||trimWord.contains("'t")
                        ||trimWord.contains("'v")
                        ||trimWord.contains("'d")
                        ||trimWord.contains("'re")
                        ||trimWord.contains("'ll")) {
                    continue;
                }

//                if (trimWord.indexOf(".") != -1
//                    || trimWord.indexOf("-") != -1
//                    || trimWord.indexOf("/") != -1) {
//                    continue;
//                }

                trimWord.toLowerCase();

                word_set.add(trimWord);
            }
        }

        return word_set;
    }
}
