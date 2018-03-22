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
        markedCorpus = new LinkedList<>();
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
            System.out.println("[ "+i+" / "+unmarkedCorpus.size()+" ] of corpus has been parsed.");
        }
    }

    //tag corpus with words in dicts, if not find ,tag with default tag.
    public void parse(Dict d, Annotation document, String defaultTag) throws IOException
    {
        List<String> words = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            words.clear();
            tags.clear();
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
            //now we have analyzed the sentence, add the results to markedCorpus
            for (int i = 0; i < words.size(); i++) {
                String[] pair = new String[2];
                pair[0] = words.get(i);
                pair[1] = tags.get(i);
                markedCorpus.add(pair);
            }
        }
    }


    public void writeMarkedCorpusToFile(String outputFileName) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Thread.currentThread().getContextClassLoader().getResource("").getFile()+outputFileName))));
        for(String[] s : markedCorpus)
            writer.write(s[0]+"\t"+s[1]+"\r\n");
        writer.close();
    }


    private List<String> unmarkedCorpus;
    //each element in the list is an array [word, tag].
    private List<String[]> markedCorpus;
}
