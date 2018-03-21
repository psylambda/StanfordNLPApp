package team.intelligenthealthcare.keywordsextraction;

import java.io.*;
import java.util.*;

public class Dict {

    //initialize the dict from a set of files
    public Dict(Map<String, String> tagToDictFileName) throws IOException {
        // 获取词典
        tagToWords = new HashMap<>();
        for (Map.Entry<String, String> tagAndDictFileName : tagToDictFileName.entrySet()) {
            String tag = tagAndDictFileName.getKey();
            String dictFileName = tagAndDictFileName.getValue();
            Set<String> dict = new HashSet<>(MyUtils.readFileAsLines(dictFileName));
            tagToWords.put(tag, dict);
        }
    }

    public Map<String, Set<String>> getDict() {
        return tagToWords;
    }

    //map a tag to a set of words
    private Map<String, Set<String>> tagToWords;

}
