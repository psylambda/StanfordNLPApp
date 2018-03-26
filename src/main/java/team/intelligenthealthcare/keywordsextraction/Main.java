package team.intelligenthealthcare.keywordsextraction;

import com.alibaba.fastjson.JSON;
import edu.stanford.nlp.ie.crf.CRFClassifier;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void testMaxMemorySize() {
        List<Byte[]> list = new LinkedList<>();
        long i = 0;
        byte j = 0;
        while (true) {
            i++;
            j++;
            System.out.println(i);
            list.add(new Byte[1 << 20]);
        }
    }
    public static void main(String[] args)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            //read the property file
            Properties property = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
            property.load(is);
            //if true, then do corpusGeneration
            if(Boolean.parseBoolean((String)property.getOrDefault("corpusGeneration", false))) {
                long startTime = System.currentTimeMillis();
                System.out.println("corpusGeneration phase starts: " + df.format(startTime));
                corpusGeneration(property);
                long endTime = System.currentTimeMillis();
                System.out.println("corpusGeneration phase ends: " + df.format(endTime));
                System.out.println("corpusGeneration phase costs " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
            }

            //if true, then do modelTraining
            if(Boolean.parseBoolean((String)property.getOrDefault("modelTraining", false))) {
                long startTime = System.currentTimeMillis();
                System.out.println("modelTraining phase starts: " + df.format(startTime));
                modelTraining(property);
                long endTime = System.currentTimeMillis();
                System.out.println("modelTraining phase ends: " + df.format(endTime));
                System.out.println("modelTraining phase costs " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
            }

            //if true, then do keywordsExtraction
            if(Boolean.parseBoolean((String)property.getOrDefault("keywordsExtraction", false))) {
                long startTime = System.currentTimeMillis();
                System.out.println("keywordsExtraction phase starts: " + df.format(startTime));
                keywordsExtraction(property);
                long endTime = System.currentTimeMillis();
                System.out.println("keywordsExtraction phase ends: " + df.format(endTime));
                System.out.println("keywordsExtraction phase costs " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("done!");
    }


    public static void corpusGeneration(Properties property) throws IOException
    {
        //read dict files
        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);

        //read corpus
        Corpus corpus = new Corpus(property.getProperty("corpusGeneration.unmarkedCorpusFileName"),property.containsKey("corpusGeneration.fileLengthEachIteration")?Integer.parseInt(property.getProperty("corpusGeneration.fileLengthEachIteration")):Integer.MAX_VALUE);
        corpus.tag(dict, property.getProperty("corpusGeneration.propertyFileName"), property.getProperty("corpusGeneration.defaultTag"));

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
