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

    //each element in the result list contains multiple lines with length closed to "len".
    public static List<String> readFileAsMultipleLines(String fileName, int len) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(fileName)));
        StringBuilder sb = new StringBuilder();
        List<String> res = new LinkedList<>();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line + "\n");
            line = reader.readLine();
            if(sb.length() >= len) {
                res.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        res.add(sb.toString());
        reader.close();
        return res;
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
    }

    public static String getClassPath()
    {
        return  Thread.currentThread().getContextClassLoader().getResource("").getPath();
    }

    public static String getAbsolutePath(String fileName)
    {
        if(fileName.charAt(0) != '/' && fileName.charAt(0) != '\\')
            fileName = getClassPath() + fileName;
        return  fileName;
    }

    public static void mkdirForAFile(String fileName)
    {
        int lastIndex = fileName.lastIndexOf('/');
        if(lastIndex != -1)
        {
            File folder = new File(fileName.substring(0, lastIndex));
            if(!folder.exists())
                folder.mkdirs();
        }

    }
}
