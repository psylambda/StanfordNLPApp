package team.intelligenthealthcare.keywordsextraction;

import java.io.*;
import java.util.*;
import com.alibaba.fastjson.JSON;
import edu.stanford.nlp.ie.crf.CRFClassifier;


public class Main {

    public static void main(String[] args)
    {

        try {
            //read the property file
            Properties property = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
            property.load(is);
            //if true, then do corpusGeneration
            if(Boolean.parseBoolean((String)property.getOrDefault("corpusGeneration", false))) {
                corpusGeneration(property);
                System.out.println("corpusGeneration successfully done!");
            }

            //if true, then do modelTraining
            if(Boolean.parseBoolean((String)property.getOrDefault("modelTraining", false))) {
                modelTraining(property);
                System.out.println("modelTraining successfully done!");
            }

            //if true, then do keywordsExtraction
            if(Boolean.parseBoolean((String)property.getOrDefault("keywordsExtraction", false))) {
                keywordsExtraction(property);
                System.out.println("keywordsExtraction successfully done!");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("end!");
    }


    public static void corpusGeneration(Properties property) throws IOException
    {
        //read dict files
        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);

        //read corpus
        Corpus corpus = new Corpus(property.getProperty("corpusGeneration.unmarkedCorpusFileName"),property.containsKey("corpusGeneration.fileLengthEachIteration")?Integer.parseInt(property.getProperty("corpusGeneration.fileLengthEachIteration")):Integer.MAX_VALUE);
        corpus.tag(dict, property.getProperty("corpusGeneration.propertyFileName"),  property.getProperty("defaultTag"));

        //write corpus
        corpus.writeMarkedCorpusToFile(property.getProperty("corpusGeneration.markedCorpusFileName"));
    }


    public static void modelTraining(Properties property) throws Exception
    {
        String[] crfArgs = new String[6];
        crfArgs[0] = "-prop";
        crfArgs[1] = property.getProperty("modelTraining.propertyFileName");
        crfArgs[2] = "-trainFile";
        crfArgs[3] = MyUtils.getClassPath()+property.getProperty("modelTraining.markedCorpusFileName");
        crfArgs[4] = "-serializeTo";
        crfArgs[5] = MyUtils.getClassPath()+property.getProperty("modelTraining.modelFileName");
        //if the directory has not been created, create one.
        MyUtils.mkdirForAFile(crfArgs[5]);
        CRFClassifier.main(crfArgs);
    }

    public static void keywordsExtraction(Properties property) throws Exception
    {
        KeywordsExtraction ext = new KeywordsExtraction();
        ext.extractKeywords(property.getProperty("keywordsExtraction.propertyFileName"), (String)property.getOrDefault("keywordsExtraction.defaultTag","O"), property.getProperty("keywordsExtraction.inputFileName"));
        String targetFileName = MyUtils.getAbsolutePath(property.getProperty("keywordsExtraction.targetFileName"));
        String keywordsFileName = MyUtils.getAbsolutePath(property.getProperty("keywordsExtraction.keywordsFileName"));
        MyUtils.mkdirForAFile(targetFileName);
        MyUtils.mkdirForAFile(keywordsFileName);
        ext.writeResults(targetFileName,keywordsFileName);
    }

}
