package reader.Helper;

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
            if (source.charAt(i) > 'A' && source.charAt(i) < 'z') {
                break;
            }
        }

        if (i == len - 1) {
            return  source;
        }
        else if (i == -1) {
            return "";
        }
        else {
            return source.substring(0, i + 1);
        }
    }
}
