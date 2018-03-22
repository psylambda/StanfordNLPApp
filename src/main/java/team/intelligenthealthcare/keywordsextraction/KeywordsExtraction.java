package team.intelligenthealthcare.keywordsextraction;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class KeywordsExtraction {
    public KeywordsExtraction()
    {
        taggedSentences = null;
        tagToWords = null;
    }
    public void extractKeywords(String propertyFileName, String defaultTag, String inputFilename) throws IOException
    {
        //run stanfordNLP, and we obtain a tag for each word in document
        Annotation document = MyUtils.AnalyzeFromFile(propertyFileName, inputFilename);

        taggedSentences = new LinkedList<>();
        tagToWords = new HashMap<>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            //we iterate over all sentences here
            //tagToWordsInSentence map a tag to big words in a sentence
            Map<String, List<String>> tagToWordsInSentence = new HashMap<>();
            StringBuilder sentenceBuilder = new StringBuilder();
            StringBuilder wordBuilder = new StringBuilder();
            String lastTag = defaultTag;

            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                //we iterate over all words and associated tags here
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String tag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                sentenceBuilder.append(word);
                //put adjacent words with the same tag together
                if(tag == lastTag)
                    wordBuilder.append(word);
                else {
                    if(!tagToWordsInSentence.containsKey(lastTag))
                        tagToWordsInSentence.put(lastTag, new LinkedList<>());
                    tagToWordsInSentence.get(lastTag).add(wordBuilder.toString());
                    lastTag = tag;
                    wordBuilder.replace(0, wordBuilder.length(),word);
                }
            }
            //fill last word
            if(wordBuilder.length() != 0)
            {
                if(!tagToWordsInSentence.containsKey(lastTag))
                    tagToWordsInSentence.put(lastTag, new LinkedList<>());
                tagToWordsInSentence.get(lastTag).add(wordBuilder.toString());
            }
            sentenceBuilder.append("\n");
            //construct the tagged sentences, and keywords files.
            for(Map.Entry<String, List<String>> entry:tagToWordsInSentence.entrySet())
            {
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
        };


    }

    public void writeResults(String targetFileName, String keywordsFileName)
    {
        try(FileOutputStream targetFile = new FileOutputStream(MyUtils.getAbsolutePath(targetFileName)))
        {
            for(String line : taggedSentences)
                targetFile.write(line.getBytes());
        } catch(IOException e)
        {
            e.printStackTrace();
        }

        //write words with the same tag to a file
        for(Map.Entry<String, Set<String>> entry : tagToWords.entrySet())
        {
            String tag = entry.getKey();
            Set<String> words = entry.getValue();
            StringBuilder sb = new StringBuilder();
            words.forEach(s->sb.append(s).append("\r\n"));
            String curKeywordsFileName = keywordsFileName.replace("*", tag);
            try(FileOutputStream curOut = new FileOutputStream(MyUtils.getAbsolutePath(curKeywordsFileName)))
            {
                curOut.write(sb.toString().getBytes());
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    List<String> taggedSentences;
    //tagToWords map a tag to big words
    Map<String, Set<String>> tagToWords;
}
