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
        String[][][] a;
        String s = "he says \"I love you.\" with a clock";
        tagger.tagByStringMatching(s);
        long startTime = System.currentTimeMillis();

        //for (int i = 0; i < 100000; i++) {
            //tagger.corpus.tagString(s);
            //String s = MyUtils.readFileAsString(property.getProperty("keywordsExtraction.inputFileName"));
            a = tagger.tagByStringMatching(s);
            for (String[][] i : a) {
                for (String[] j : i) {
                    for (String k : j)
                        System.out.print(k + "\t");
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


        //long endTime = System.currentTimeMillis();
        //System.out.println("100000 iteration " + Float.toString((endTime - startTime) / 1000F) + " seconds.");

//        startTime = System.currentTimeMillis();
//
//        StringBuilder s1000 = new StringBuilder();
//        for (int i = 0; i < 100000; i++) {
//            s1000.append(s);
//
//        }
//        tagger.corpus.tagString(s1000.toString());
//        endTime = System.currentTimeMillis();
//        System.out.println("1 iteration for 100000String " + Float.toString((endTime - startTime) / 1000F) + " seconds.");
//        String[][][] b = tagger.tagByNER(s);
//
////        for(int i = 0; i < 100; i++)
////            b = tagByNER(s);
//
//        for (String[][] i : b) {
//            for (String[] j : i) {
//                for (String k : j)
//                    System.out.print(k + "\t");
//                System.out.print("\n");
//            }
//            System.out.print("\n");
//        }
    }



}
