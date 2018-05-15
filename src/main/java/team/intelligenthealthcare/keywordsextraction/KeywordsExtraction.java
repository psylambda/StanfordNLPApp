package team.intelligenthealthcare.keywordsextraction;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class KeywordsExtraction {
    private final StanfordCoreNLP corenlp;
    private final String defaultTag;
    public KeywordsExtraction(String propertyFileName, String defaultTag)
    {
        this.corenlp = new StanfordCoreNLP(propertyFileName);
        this.defaultTag = defaultTag;
//        taggedSentences = new LinkedList<>();
//        tagToWords = new HashMap<>();
    }




    public List<List<List<String>>> tagString(String text) throws IOException {
        Annotation document = new Annotation(text);
        if(text.isEmpty()) return new ArrayList<>();
        corenlp.annotate(document);

        List<String> taggedSentences = new LinkedList<>();
        Map<String, Set<String>> tagToWords = new HashMap<>();
        List<List<List<String>>> result = new ArrayList<>();
        if(document.get(CoreAnnotations.SentencesAnnotation.class) == null) return new ArrayList<>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            //we iterate over all sentences here
            //tagToWordsInSentence map a tag to big words in a sentence
            Map<String, List<String>> tagToWordsInSentence = new HashMap<>();
            StringBuilder sentenceBuilder = new StringBuilder();
            StringBuilder wordBuilder = new StringBuilder();
            String lastTag = defaultTag;
            List<String> wordList = new ArrayList<>();
            List<String> tagList = new ArrayList<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                //we iterate over all words and associated tags here
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String tag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                sentenceBuilder.append(word);
                //put adjacent words with the same tag together
                if (!tag.equals(defaultTag) && tag.equals(lastTag))
                    wordBuilder.append(word);
                else {
                    if (wordBuilder.length() != 0) {
                        if (!tagToWordsInSentence.containsKey(lastTag))
                            tagToWordsInSentence.put(lastTag, new LinkedList<>());

                        tagToWordsInSentence.get(lastTag).add(wordBuilder.toString());
                        wordList.add(wordBuilder.toString());
                        tagList.add(lastTag);
                        wordBuilder.replace(0, wordBuilder.length(), word);
                    } else
                        wordBuilder.append(word);
                    lastTag = tag;
                }
            }
            //fill last word
            if(wordBuilder.length() != 0) {
                if(!tagToWordsInSentence.containsKey(lastTag))
                    tagToWordsInSentence.put(lastTag, new LinkedList<>());
                tagToWordsInSentence.get(lastTag).add(wordBuilder.toString());

                //construct wordToTag
                wordList.add(wordBuilder.toString());
                tagList.add(lastTag);
            }
            sentenceBuilder.append("\n");
            //construct the tagged sentences, and keywords files.
            for(Map.Entry<String, List<String>> entry:tagToWordsInSentence.entrySet()) {
                String tag = entry.getKey();
                List<String> words = entry.getValue();
                //skip the default tag
                if(tag.equals(defaultTag)) continue;
                //construct a line with a tag and corresponding words
                sentenceBuilder.append("* ").append(tag).append(" :");
                words.forEach(word->sentenceBuilder.append("  ").append(word));
                sentenceBuilder.append("\n");
                //insert the current words in the smallmap to the bigmap
                if(!tagToWords.containsKey(tag))
                    tagToWords.put(tag, new HashSet<>());
                tagToWords.get(tag).addAll(words);
            }
            taggedSentences.add(sentenceBuilder.append("\n").toString());

            //construct result
            result.add(new ArrayList<>());
            result.get(result.size()-1).add(wordList);
            result.get(result.size()-1).add(tagList);
        }
        return result;
    }

    public  List<List<List<String>>> tagFile(String inputFilename) throws IOException {
        return tagString(MyUtils.readFileAsString(inputFilename));
    }

//    public void writeResults(String targetFileName, String keywordsFileName) {
//        try (FileOutputStream targetFile = new FileOutputStream(MyUtils.getAbsolutePath(targetFileName))) {
//            for (String line : taggedSentences)
//                targetFile.write(line.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //write words with the same tag to a file
//        for (Map.Entry<String, Set<String>> entry : tagToWords.entrySet()) {
//            String tag = entry.getKey();
//            Set<String> words = entry.getValue();
//            StringBuilder sb = new StringBuilder();
//            words.forEach(s -> sb.append(s).append("\r\n"));
//            String curKeywordsFileName = keywordsFileName.replace("*", tag);
//            try (FileOutputStream curOut = new FileOutputStream(MyUtils.getAbsolutePath(curKeywordsFileName))) {
//                curOut.write(sb.toString().getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //List<String[][]> result;

    //List<String> taggedSentences;

//    private void extractKeywords( Annotation document) throws IOException {
//
//        ;
//
//
//    }
    //tagToWords map a tag to big words
    //Map<String, Set<String>> tagToWords;

    //public List<String[][]> getResult() {
        //return result;
    //}
}
