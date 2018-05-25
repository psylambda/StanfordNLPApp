package team.intelligenthealthcare.keywordsextraction;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.io.IOException;
import java.util.*;


public class Dict {

    private Trie trie;
    private String keywordTag;
    private String defaultTag = "O";
    //initialize the dict from a set of files
    public Dict(Map<String, String> tagToDictFileName) throws IOException {
        //only add the first dict
        Trie.TrieBuilder trieBuilder = Trie.builder().ignoreOverlaps();
        for (Map.Entry<String, String> tagAndDictFileName : tagToDictFileName.entrySet()) {
            keywordTag = tagAndDictFileName.getKey();
            String dictFileName = tagAndDictFileName.getValue();
            Set<String> dict = new HashSet<>(MyUtils.readFileAsLines(dictFileName));
            trieBuilder.addKeywords(MyUtils.readFileAsLines(dictFileName));
            break;
        }
        trie = trieBuilder.build();
    }

    //return two lists  words&tags
    public List<List<String>> ahocorasickParse(String text) {
        Collection<Emit> emits = trie.parseText(text);
        final List<String> words = new ArrayList<>(), tags = new ArrayList<>();
        int curStart = 0;
        for (Emit emit : emits) {
            if (curStart != emit.getStart()) {
                words.add(text.substring(curStart, emit.getStart()));
                tags.add(defaultTag);
            }
            words.add(emit.getKeyword());
            tags.add(keywordTag);
            curStart = emit.getEnd() + 1;
        }
        if (curStart < text.length()) {
            words.add(text.substring(curStart));
            tags.add(defaultTag);
        }
        return new ArrayList<List<String>>() {{
            add(words);
            add(tags);
        }};
    }

}
