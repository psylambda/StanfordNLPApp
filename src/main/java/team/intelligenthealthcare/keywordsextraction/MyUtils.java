package team.intelligenthealthcare.keywordsextraction;

import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.*;

public class MyUtils {
    public static String readFileAsString(String fileName) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(fileName)));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line + "\n");
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    //skip empty lines
    public static List<String> readFileAsLines(String fileName) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(fileName)));
        List<String> res = new LinkedList<>();
        String s = reader.readLine();
        while (s != null && !s.isEmpty()) {
            res.add(s);
            s = reader.readLine();
        }
        reader.close();
        return res;
    }

//    public static void writeFile(String fileName, String content) throws IOException
//    {
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource(fileName).getPath())));
//        writer.write(content);
//        writer.close();
//    }

    public static Annotation AnalyzeFromString(String propertyFileName, String text)
    {
        Annotation document = new Annotation(text);
        StanfordCoreNLP corenlp = new StanfordCoreNLP(propertyFileName);
        corenlp.annotate(document);
        return document;
    }

    public static Annotation AnalyzeFromFile(String propertyFileName, String inputFileName) throws IOException
    {
        return  AnalyzeFromString(propertyFileName, readFileAsString(inputFileName));
        //StanfordCoreNLP corenlp = new StanfordCoreNLP(propertyFileName);
        //corenlp.processFiles(new File(Thread.currentThread().getContextClassLoader().getResource("").getFile()+outputFileName));
    }
}
