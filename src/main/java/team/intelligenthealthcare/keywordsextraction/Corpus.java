package team.intelligenthealthcare.keywordsextraction;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.*;

public class Corpus {

    private List<List<String>> allWords = new ArrayList<>();
    private List<List<String>> allTags = new ArrayList<>();

    //input is just the content
    public Corpus(String input) {
        unmarkedCorpus = new ArrayList<>();
        unmarkedCorpus.add(input);
    }

    //s is the file name, len is max length of each file
    public Corpus(String s, int len)throws IOException {
        unmarkedCorpus = MyUtils.readFileAsMultipleLines(s, len);
    }

    //run stanfordCoreNLP
    //segmentationPropertyFileName is the name of property file for stanfordNLP.
    public void tag(Dict d, String segmentationPropertyFileName, String defaultTag) throws IOException
    {
        StanfordCoreNLP corenlp = new StanfordCoreNLP(segmentationPropertyFileName);
        int i = 0;
        for(String text : unmarkedCorpus) {
            Annotation document = new Annotation(text);
            corenlp.annotate(document);
            parse(d, document, defaultTag);
            i++;
            System.out.println("[ " + i + " / " + unmarkedCorpus.size() + " ] of corpus has been analyzed. Totally " + text.length() + " bytes. " + new Date());
        }
    }

    //tag corpus with words in dicts, if not find ,tag with default tag.
    public void parse(Dict d, Annotation document, String defaultTag) throws IOException
    {
        List<String> words, tags;
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            words = new ArrayList<>();
            tags = new ArrayList<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                //get each word in a sentence
                words.add(token.get(CoreAnnotations.TextAnnotation.class));
                //tag it with default tag
                tags.add(defaultTag);
            }
            //tag by matching consecutive words with dicts
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.size(); i++) {
                sb.delete(0, sb.length());
                for (int j = i; j < words.size(); j++) {
                    sb.append(words.get(j));
                    //now sb contains consecutive words "words[i]+words[i+1]...words[j]"
                    //If that big word is in a dict, tag it
                    for (Map.Entry<String, Set<String>> tagAndDict : d.getDict().entrySet()) {
                        String tag = tagAndDict.getKey();
                        Set<String> dict = tagAndDict.getValue();
                        if (dict.contains(sb.toString())) {
                            //if the big word has been tagged already, then skip it.
                            //otherwise, tag it.
                            boolean hasBeenTagged = false;
                            for (int k = i; k <= j; k++)
                                if(!tags.get(k).equals(defaultTag)) {
                                    hasBeenTagged = true;
                                    break;
                                }
                            if(!hasBeenTagged) {
                                for (int k = i; k <= j; k++)
                                    tags.set(k, tag);
                            }
                        }
                    }
                }
            }

            allWords.add(words);
            allTags.add(tags);
        }

    }

    //now we have analyzed the small file, write the results.
    public void writeMarkedCorpusToFile(String markedCorpusFileName) throws IOException
    {
        //System.out.println("write marked corpus "+outputFileName);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Thread.currentThread().getContextClassLoader().getResource("").getFile() + markedCorpusFileName))));

        for (int i = 0; i < Math.min(allWords.size(), allTags.size()); i++) {
            List<String> wordsInASentence = allWords.get(i);
            List<String> tagsInASentence = allTags.get(i);
            for (int j = 0; j < Math.min(wordsInASentence.size(), tagsInASentence.size()); j++) {
                writer.write(wordsInASentence.get(j) + "\t" + tagsInASentence.get(j) + "\r\n");
            }
            writer.write("\r\n");
        }

        writer.flush();
        writer.close();
    }

    private List<String> unmarkedCorpus;

    public List<List<String>> getAllWords() {
        return allWords;
    }

    public List<List<String>> getAllTags() {
        return allTags;
    }
}
