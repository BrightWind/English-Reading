package reader.Helper;

import java.io.File;

public class StaticConfigration {
    public static boolean ExistPath(String path) {
        File dir = new File(path);
        return dir.exists();
    }

    public static String ResourcePath () {
        String path = "/root/resources";
        String path1 = "C:\\Users\\mark00x\\Desktop\\English-Reading\\service\\src\\main\\resources";
        String path2 = "E:\\Projects\\EnglishReader\\English-Reading\\service\\src\\main\\resources";
        if (ExistPath(path)) {
            return path;
        }

        if (ExistPath(path1)) {
            return path1;
        }

        if (ExistPath(path2)) {
            return path2;
        }

        return null;
    }
}
