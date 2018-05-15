package team.intelligenthealthcare.keywordsextraction;

import com.alibaba.fastjson.JSON;
import edu.stanford.nlp.ie.crf.CRFClassifier;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tagger {
    private Corpus corpus;
    private KeywordsExtraction ext;
    private String defaultTag;
    public Tagger() throws IOException {
        Properties property = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(is);
        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);
        defaultTag = property.getProperty("corpusGeneration.defaultTag");
        corpus = new Corpus(property.getProperty("corpusGeneration.propertyFileName"),dict, defaultTag);


        Properties propertyNER = new Properties();
        InputStream isNER = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(isNER);
        ext = new KeywordsExtraction(property.getProperty("keywordsExtraction.propertyFileName"), (String) property.getOrDefault("keywordsExtraction.defaultTag", "O"));

    }
    /**
     * Return an array of tagged words by simply matching for words in dicts
     *
     * @Return in ith sentence and jth word, res[i][0][j]:word   res[i][1][j]:tag
     */
    public String[][][] tagByStringMatching(String input) throws IOException {
        List<List<List<String>>> result = corpus.tagString(input);


        //prepare to return
        List<List<String>> allWords = new ArrayList<>();
        List<List<String>> allTags = new ArrayList<>();
        for(List<List<String>> sentence : result)
        {
            allWords.add(sentence.get(0));
            allTags.add(sentence.get(1));
        }
        List<String[][]> res = new ArrayList<>();
        for (int i = 0; i < allWords.size(); i++) {
            StringBuilder wordBuilder = new StringBuilder();
            String lastTag = defaultTag;
            List<String> wordList = new ArrayList<>();
            List<String> tagList = new ArrayList<>();
            for (int j = 0; j < allWords.get(i).size(); j++) {
                String word = allWords.get(i).get(j);
                String tag = allTags.get(i).get(j);
                //put adjacent words with the same tag together
                if (!tag.equals(defaultTag) && tag.equals(lastTag))
                    wordBuilder.append(word);
                else {
                    if (wordBuilder.length() > 0) {
                        //construct wordToTag
                        wordList.add(wordBuilder.toString());
                        tagList.add(lastTag);
                        wordBuilder.replace(0, wordBuilder.length(), word);
                    } else
                        wordBuilder.append(word);
                    lastTag = tag;
                }
            }
            //fill last word
            if (wordBuilder.length() != 0) {
                wordList.add(wordBuilder.toString());
                tagList.add(lastTag);
            }
            String[][] resInSentence = new String[2][wordList.size()];
            resInSentence[0] = wordList.toArray(resInSentence[0]);
            resInSentence[1] = tagList.toArray(resInSentence[1]);
            res.add(resInSentence);
        }
        return res.toArray(new String[res.size()][][]);
    }

    /**
     * Return an array of tagged words by NER
     *
     * @Return res[i][0]:word   res[i][1]:tag
     */
    public String[][][] tagByNER(String input) throws IOException {
        List<List<List<String>>> result = ext.tagString(input);
        String[][][] res = new String[result.size()][2][];
        for(int i = 0; i < result.size(); i++)
        {
            res[i][0] = new String[result.get(i).get(0).size()];
            res[i][1] = new String[result.get(i).get(1).size()];
            for(int j = 0; j < result.get(i).get(0).size(); j++)
            {
                res[i][0][j] = result.get(i).get(0).get(j);
                res[i][1][j] = result.get(i).get(1).get(j);
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        //read dict files
        Tagger tagger = new Tagger();
        Properties property = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(is);

        String s = "慢性支气管炎(chronic bronchitis)是指气管、支气管黏膜及其周围组织的慢\n" +
                "性非特异性炎症。临床上以反复发作的咳嗽、咳痰或伴有喘鸣音为特征。上述临床症状每年持续3个月，连续发生2年以上，即可诊断为慢性支气管炎。";
        //String s = MyUtils.readFileAsString(property.getProperty("keywordsExtraction.inputFileName"));
        String[][][] a = tagger.tagByStringMatching(s);
        for(String[][] i : a)
        {
            for(String[] j: i)
            {
                for(String k: j)
                    System.out.print(k+"\t");
                System.out.print("\n");
            }
            System.out.print("\n");
        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");


        String[][][] b = tagger.tagByNER(s);

//        for(int i = 0; i < 100; i++)
//            b = tagByNER(s);

        for (String[][] i : b) {
            for (String[] j : i) {
                for (String k : j)
                    System.out.print(k + "\t");
                System.out.print("\n");
            }
            System.out.print("\n");
        }
    }

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

    public static void mainx(String[] args) {
//        String path = String.format("%s/%s", System.getProperty("user.dir"), Tagger.class.getPackage().getName().replace(".", "/"));
//        System.out.println(path);
//        System.out.println(System.getProperty("user.dir"));
//        System.out.println(Tagger.class.getPackage().getName());
        ner(args);
    }

    public static void ner(String[] args) {
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
        Corpus corpus = new Corpus(property.getProperty("corpusGeneration.propertyFileName"), dict, property.getProperty("corpusGeneration.defaultTag"));
        List<List<List<String>>> result = corpus.tagFile(property.getProperty("corpusGeneration.unmarkedCorpusFileName"), property.containsKey("corpusGeneration.fileLengthEachIteration") ? Integer.parseInt(property.getProperty("corpusGeneration.fileLengthEachIteration")) : Integer.MAX_VALUE);

        //write to file
        String outputFileName = property.getProperty("corpusGeneration.markedCorpusFileNames");
        System.out.println("write marked corpus "+outputFileName);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Thread.currentThread().getContextClassLoader().getResource("").getFile() + outputFileName))));
        for (int i = 0; i < result.size(); i++) {
            List<String> wordsInASentence = result.get(i).get(0);
            List<String> tagsInASentence = result.get(i).get(1);
            for (int j = 0; j < Math.min(wordsInASentence.size(), tagsInASentence.size()); j++) {
                writer.write(wordsInASentence.get(j) + "\t" + tagsInASentence.get(j) + "\r\n");
            }
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
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
//        KeywordsExtraction ext = new KeywordsExtraction();
//        ext.extractKeywordsFromFile(property.getProperty("keywordsExtraction.propertyFileName"), (String) property.getOrDefault("keywordsExtraction.defaultTag", "O"), property.getProperty("keywordsExtraction.inputFileName"));
//        String targetFileName = MyUtils.getAbsolutePath(property.getProperty("keywordsExtraction.targetFileName"));
//        String keywordsFileName = MyUtils.getAbsolutePath(property.getProperty("keywordsExtraction.keywordsFileName"));
//        MyUtils.mkdirForAFile(targetFileName);
//        MyUtils.mkdirForAFile(keywordsFileName);
//        ext.writeResults(targetFileName, keywordsFileName);
    }

}
