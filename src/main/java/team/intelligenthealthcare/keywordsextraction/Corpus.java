package team.intelligenthealthcare.keywordsextraction;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.*;

public class Corpus {

    public Corpus(String s, int len)throws IOException {
        unmarkedCorpus = MyUtils.readFileAsMultipleLines(s, len);
    }

    //run stanfordCoreNLP
    //segmentationPropertyFileName is the name of property file for stanfordNLP.
    public void tag(Dict d, String segmentationPropertyFileName, String defaultTag, String markedCorpusFileNames) throws IOException
    {
        StanfordCoreNLP corenlp = new StanfordCoreNLP(segmentationPropertyFileName);
        int i = 0;
        for(String text : unmarkedCorpus) {
            Annotation document = new Annotation(text);
            corenlp.annotate(document);
            parse(d, document, defaultTag, markedCorpusFileNames.replace("*", String.valueOf(i + 1)));
            i++;
            System.out.println("[ " + i + " / " + unmarkedCorpus.size() + " ] of corpus has been analyzed. Totally " + text.length() + " bytes. " + new Date());
        }
    }

    //tag corpus with words in dicts, if not find ,tag with default tag.
    public void parse(Dict d, Annotation document, String defaultTag, String markedCorpusFileName) throws IOException
    {
        List<String> words = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<List<String>> allWords = new ArrayList<>();
        List<List<String>> allTags = new ArrayList<>();
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
        //now we have analyzed the small file, write the results.
        writeMarkedCorpusToFile(markedCorpusFileName, allWords, allTags);
    }


    public void writeMarkedCorpusToFile(String outputFileName, List<List<String>> words, List<List<String>> tags) throws IOException
    {
        //System.out.println("write marked corpus "+outputFileName);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Thread.currentThread().getContextClassLoader().getResource("").getFile() + outputFileName))));

        for (int i = 0; i < Math.min(words.size(), tags.size()); i++) {
            List<String> wordsInASentence = words.get(i);
            List<String> tagsInASentence = tags.get(i);
            for (int j = 0; j < Math.min(wordsInASentence.size(), tagsInASentence.size()); j++) {
                writer.write(wordsInASentence.get(j) + "\t" + tagsInASentence.get(j) + "\r\n");
            }
            writer.write("\r\n");
        }

        writer.flush();
        writer.close();
    }


    private List<String> unmarkedCorpus;
}
