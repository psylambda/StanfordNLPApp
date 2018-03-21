package team.intelligenthealthcare.keywordsextraction;

import java.io.*;
import java.util.*;
import com.alibaba.fastjson.JSON;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


public class Main {

    public static void mainx(String[] args) {
        try {
            StanfordCoreNLP corenlp = new StanfordCoreNLP("stanfordNLP/segmentation.properties");
            List<File> files = new ArrayList<>();
            files.add(new File("C:\\Users\\wangy\\IdeaProjects\\StanfordNLPApp\\src\\main\\resources\\corpus\\smallCorpus.txt"));
            corenlp.processFiles(files, false);
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("successfully done!");
    }
    public static void main(String[] args)
    {
        try {
            //read the property file
            Properties property = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
            property.load(is);

            //if true, then do corpusGeneration
            if(Boolean.parseBoolean((String)property.getOrDefault("corpusGeneration", false))) {
                //read dict files
                String str = property.getProperty("dict");
                Map<String, String> m = JSON.parseObject(str, Map.class);
                Dict dict = new Dict(m);

                //read corpus
                Corpus corpus = new Corpus(property.getProperty("unmarkedCorpusFileName"));
                corpus.mark(dict, property.getProperty("segmentationPropertyFileName"),  property.getProperty("defaultTag"));
                corpus.writeMarkedCorpusToFile(property.getProperty("markedCorpusFileName"));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("successfully done!");
    }
}
