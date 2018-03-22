package team.intelligenthealthcare.keywordsextraction;

import java.io.*;
import java.util.*;
import com.alibaba.fastjson.JSON;
import edu.stanford.nlp.ie.crf.CRFClassifier;


public class Main {

//    public static void mainx(String[] args) {
//        try {
//            StanfordCoreNLP corenlp = new StanfordCoreNLP("stanfordNLP/segmentation.properties");
//            List<File> files = new ArrayList<>();
//            files.add(new File("C:\\Users\\wangy\\IdeaProjects\\StanfordNLPApp\\src\\main\\resources\\corpus\\smallCorpus.txt"));
//            corenlp.processFiles(files, false);
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("successfully done!");
//    }
    public static void main(String[] args)
    {
        //MyUtils.mkdirIfNotExists("/C:/Users/wangy/IdeaProjects/StanfordNLPApp/target/classes/model/");
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
        String str = property.getProperty("dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);

        //read corpus
        Corpus corpus = new Corpus(property.getProperty("unmarkedCorpusFileName"));
        corpus.mark(dict, property.getProperty("segmentationPropertyFileName"),  property.getProperty("defaultTag"));

        //write corpus
        corpus.writeMarkedCorpusToFile(property.getProperty("markedCorpusFileName"));
    }


    public static void modelTraining(Properties property) throws Exception
    {
        String[] crfArgs = new String[6];
        crfArgs[0] = "-prop";
        crfArgs[1] = property.getProperty("modelTrainingPropertyFileName");
        crfArgs[2] = "-trainFile";
        crfArgs[3] = MyUtils.getClassPath()+property.getProperty("markedCorpusFileName");
        crfArgs[4] = "-serializeTo";
        crfArgs[5] = MyUtils.getClassPath()+property.getProperty("modelFileName");
        //if the directory has not been created, create one.
        MyUtils.mkdirIfNotExists(crfArgs[5].substring(0, crfArgs[5].lastIndexOf('/')+1));
        CRFClassifier.main(crfArgs);
    }

    public static void keywordsExtraction(Properties property) throws Exception
    {
        String[] crfArgs = new String[6];
        crfArgs[0] = "-prop";
        crfArgs[1] = property.getProperty("modelTrainingPropertyFileName");
        crfArgs[2] = "-trainFile";
        crfArgs[3] = MyUtils.getClassPath()+property.getProperty("markedCorpusFileName");
        crfArgs[4] = "-serializeTo";
        crfArgs[5] = MyUtils.getClassPath()+property.getProperty("modelFileName");
        //if the directory has not been created, create one.
        MyUtils.mkdirIfNotExists(crfArgs[5].substring(0, crfArgs[5].lastIndexOf('/')+1));
        CRFClassifier.main(crfArgs);
    }

}
