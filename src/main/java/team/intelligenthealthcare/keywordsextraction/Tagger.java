package team.intelligenthealthcare.keywordsextraction;

import com.alibaba.fastjson.JSON;
import edu.stanford.nlp.ie.crf.CRFClassifier;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tagger {

    /**
     * Return an array of tagged words by simply matching for words in dicts
     *
     * @Return res[i][0]:word   res[i][1]:tag
     */
    public static String[][] tagByStringMatching(String input) throws IOException {
        //read dict files
        Properties property = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(is);

        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);

        //read corpus
        Corpus corpus = new Corpus(input);
        corpus.tag(dict, property.getProperty("corpusGeneration.propertyFileName"), property.getProperty("corpusGeneration.defaultTag"));


        //prepare to return
        List<List<String>> allWords = corpus.getAllWords();
        List<List<String>> allTags = corpus.getAllTags();
        List<String[]> wordToTag = new ArrayList<>();
        String defaultTag = property.getProperty("keywordsExtraction.defaultTag");
        for (int i = 0; i < allWords.size(); i++) {
            StringBuilder wordBuilder = new StringBuilder();
            String lastTag = defaultTag;
            for (int j = 0; j < allWords.get(i).size(); j++) {
                String word = allWords.get(i).get(j);
                String tag = allTags.get(i).get(j);
                //put adjacent words with the same tag together
                if (!tag.equals(defaultTag) && tag.equals(lastTag))
                    wordBuilder.append(word);
                else {
                    if (wordBuilder.length() > 0) {
                        //construct wordToTag
                        String[] wordAndTag = new String[2];
                        wordAndTag[0] = wordBuilder.toString();
                        wordAndTag[1] = lastTag;
                        wordToTag.add(wordAndTag);
                        wordBuilder.replace(0, wordBuilder.length(), word);
                    } else
                        wordBuilder.append(word);
                    lastTag = tag;
                }
            }
            //fill last word
            if (wordBuilder.length() != 0) {
                //construct wordToTag
                String[] wordAndTag = new String[2];
                wordAndTag[0] = wordBuilder.toString();
                wordAndTag[1] = lastTag;
                wordToTag.add(wordAndTag);
            }
//            String[] wordAndTag = new String[2];
//            wordAndTag[0] = "sentence";
//            wordAndTag[1] = "sentence";
//            wordToTag.add(wordAndTag);
        }


//        allWords.forEach(sentence -> words.addAll(sentence));
//        allTags.forEach(sentence -> tags.addAll(sentence));
//        String[][] res = new String[words.size()][2];
//        for (int i = 0; i < words.size(); i++) {
//            res[i][0] = words.get(i);
//            res[i][1] = tags.get(i);
        return wordToTag.toArray(new String[0][]);
    }

    /**
     * Return an array of tagged words by NER
     *
     * @Return res[i][0]:word   res[i][1]:tag
     */
    public static String[][] tagByNER(String input) throws IOException {
        //read dict files
        Properties property = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(is);

        KeywordsExtraction ext = new KeywordsExtraction();
        ext.extractKeywordsFromString(property.getProperty("keywordsExtraction.propertyFileName"), (String) property.getOrDefault("keywordsExtraction.defaultTag", "O"), input);
        return ext.getWordToTag().toArray(new String[0][]);
    }

//    public static void main(String[] args) throws IOException{
//        //read dict files
//        Properties property = new Properties();
//        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
//        property.load(is);
//
//        String s="慢性支气管炎以咳嗽、咳痰或伴有喘鸣音为特征。";
//        //String s = MyUtils.readFileAsString(property.getProperty("keywordsExtraction.inputFileName"));
//        String[][] a = tagByStringMatching(s);
//       String[][] b = tagByNER(s);
//
//        for(String[] i : a)
//        {
//            System.out.println(i[0]+"\t"+i[1]);
//        }
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        for(String[] i : b)
//        {
//            System.out.println(i[0]+"\t"+i[1]);
//        }
//    }

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

    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            //read the property file
            Properties property = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
            property.load(is);
            //if true, then do corpusGeneration
            if (Boolean.parseBoolean((String) property.getOrDefault("corpusGeneration", false))) {
                long startTime = System.currentTimeMillis();
                System.out.println("corpusGeneration phase starts: " + df.format(startTime));
                corpusGeneration(property);
                long endTime = System.currentTimeMillis();
                System.out.println("corpusGeneration phase ends: " + df.format(endTime));
                System.out.println("corpusGeneration phase costs " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
            }

            //if true, then do modelTraining
            if (Boolean.parseBoolean((String) property.getOrDefault("modelTraining", false))) {
                long startTime = System.currentTimeMillis();
                System.out.println("modelTraining phase starts: " + df.format(startTime));
                modelTraining(property);
                long endTime = System.currentTimeMillis();
                System.out.println("modelTraining phase ends: " + df.format(endTime));
                System.out.println("modelTraining phase costs " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
            }

            //if true, then do keywordsExtraction
            if (Boolean.parseBoolean((String) property.getOrDefault("keywordsExtraction", false))) {
                long startTime = System.currentTimeMillis();
                System.out.println("keywordsExtraction phase starts: " + df.format(startTime));
                keywordsExtraction(property);
                long endTime = System.currentTimeMillis();
                System.out.println("keywordsExtraction phase ends: " + df.format(endTime));
                System.out.println("keywordsExtraction phase costs " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done!");
    }


    public static void corpusGeneration(Properties property) throws IOException {
        //read dict files
        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);

        //read corpus
        Corpus corpus = new Corpus(property.getProperty("corpusGeneration.unmarkedCorpusFileName"), property.containsKey("corpusGeneration.fileLengthEachIteration") ? Integer.parseInt(property.getProperty("corpusGeneration.fileLengthEachIteration")) : Integer.MAX_VALUE);
        corpus.tag(dict, property.getProperty("corpusGeneration.propertyFileName"), property.getProperty("corpusGeneration.defaultTag"));
        corpus.writeMarkedCorpusToFile(property.getProperty("corpusGeneration.markedCorpusFileNames"));
    }


    public static void modelTraining(Properties property) throws Exception {
        String[] crfArgs = new String[6];
        crfArgs[0] = "-prop";
        crfArgs[1] = property.getProperty("modelTraining.propertyFileName");
        crfArgs[2] = "-trainFileList";
        crfArgs[3] = MyUtils.getClassPath() + property.getProperty("modelTraining.markedCorpusFileNames");
        //crfArgs[3] = MyUtils.getAllFileName(MyUtils.getClassPath() + property.getProperty("modelTraining.markedCorpusFileNames"));
        crfArgs[4] = "-serializeTo";
        crfArgs[5] = MyUtils.getClassPath() + property.getProperty("modelTraining.modelFileName");
        //if the directory has not been created, create one.
        MyUtils.mkdirForAFile(crfArgs[5]);


        CRFClassifier.main(crfArgs);


    }

    public static void keywordsExtraction(Properties property) throws Exception {
        KeywordsExtraction ext = new KeywordsExtraction();
        ext.extractKeywordsFromFile(property.getProperty("keywordsExtraction.propertyFileName"), (String) property.getOrDefault("keywordsExtraction.defaultTag", "O"), property.getProperty("keywordsExtraction.inputFileName"));
        String targetFileName = MyUtils.getAbsolutePath(property.getProperty("keywordsExtraction.targetFileName"));
        String keywordsFileName = MyUtils.getAbsolutePath(property.getProperty("keywordsExtraction.keywordsFileName"));
        MyUtils.mkdirForAFile(targetFileName);
        MyUtils.mkdirForAFile(keywordsFileName);
        ext.writeResults(targetFileName, keywordsFileName);
    }

}
