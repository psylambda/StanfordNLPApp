package team.intelligenthealthcare.keywordsextraction;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class MediHist {
    public static void main(String[] args) throws IOException {
        String curInput,outputFileName;
        StringBuilder sb, output;
        String[][][] tagByStringMatch, tagByNER;
        Set<String> curKeywordsByStringMatch, curKeywordsByNER;
        Set<String> batchKeywordsByStringMatch = new HashSet<>(), batchKeywordsByNER = new HashSet<>();
        Tagger tagger = new Tagger();
        //
        File[] faFiles = new File("./src/main/resources/input/").listFiles();
        for(File file: faFiles){
            if(file.getName().matches("(.*)txt")){
                //file = new File("/home/ner/IdeaProjects/StanfordNLPApp/./src/main/resources/input/04.txt");
                System.out.println(file.getAbsolutePath());

                curInput = readFile(file);

                tagByStringMatch = tagger.tagByStringMatching(curInput);
                tagByNER = tagger.tagByNER(curInput);

                //write to  StringMatch/Mapping-01.txt
                sb = new StringBuilder(file.getAbsolutePath().replace("input","StringMatch"));
                sb.insert(sb.lastIndexOf("/")+1, "Mapping-");
                outputFileName = sb.toString();
                output = new StringBuilder();
                curKeywordsByStringMatch = new HashSet<>();
                for(String[][] sentence : tagByStringMatch)
                {
                    for(int i = 0; i < sentence[0].length; i++)
                    {
                        sb.append(sentence[1][i].equals("O")?0:1).append('\t').append(sentence[0][i]).append("\r\n");
                        if(!sentence[1][i].equals("O"))
                            curKeywordsByStringMatch.add(sentence[0][i]);
                    }
                }
                batchKeywordsByStringMatch.addAll(curKeywordsByStringMatch);
                Files.write(Paths.get(outputFileName), output.toString().getBytes());


                //write to  StringMatch/Entity-01.txt
                sb = new StringBuilder(file.getAbsolutePath().replace("input","StringMatch"));
                sb.insert(sb.lastIndexOf("/")+1, "Entity-");
                outputFileName = sb.toString();
                output = new StringBuilder();
                for(String s: curKeywordsByStringMatch)
                    output.append(s).append("\r\n");
                Files.write(Paths.get(outputFileName), output.toString().getBytes());


                //write to  NER/Mapping-01.txt
                sb = new StringBuilder(file.getAbsolutePath().replace("input","NER"));
                sb.insert(sb.lastIndexOf("/")+1, "Mapping-");
                outputFileName = sb.toString();
                output = new StringBuilder();
                curKeywordsByNER = new HashSet<>();
                for(String[][] sentence : tagByNER)
                {
                    for(int i = 0; i < sentence[0].length; i++)
                    {
                        sb.append(sentence[1][i].equals("O")?0:1).append('\t').append(sentence[0][i]).append("\r\n");
                        if(!sentence[1][i].equals("O"))
                            curKeywordsByNER.add(sentence[0][i]);
                    }
                }
                batchKeywordsByNER.addAll(curKeywordsByNER);
                Files.write(Paths.get(outputFileName), output.toString().getBytes());


                //write to  NER/Entity-01.txt
                sb = new StringBuilder(file.getAbsolutePath().replace("input","NER"));
                sb.insert(sb.lastIndexOf("/")+1, "Entity-");
                outputFileName = sb.toString();
                output = new StringBuilder();
                for(String s: curKeywordsByNER)
                    output.append(s).append("\r\n");
                Files.write(Paths.get(outputFileName), output.toString().getBytes());


                //write to  Difference/01.txt
                outputFileName = file.getAbsolutePath().replace("input","Difference");
                output = new StringBuilder();
                output.append("NER - StringMatch\r\n");
                for(String s : curKeywordsByNER)
                    if(!curKeywordsByStringMatch.contains(s))
                        output.append(s).append("\r\n");
                output.append("StringMatch - NER\r\n");
                for(String s : curKeywordsByStringMatch)
                    if(!curKeywordsByNER.contains(s))
                        output.append(s).append("\r\n");
                Files.write(Paths.get(outputFileName), output.toString().getBytes());
            }
        }
        //write to  KeywordsBatch/StringMatch.txt
        outputFileName = "./src/main/resources/KeywordsBatch/StringMatch.txt";
        output = new StringBuilder();
        for(String s : batchKeywordsByStringMatch)
            output.append(s).append("\r\n");
        Files.write(Paths.get(outputFileName), output.toString().getBytes());
        //write to  KeywordsBatch/NER.txt
        outputFileName = "./src/main/resources/KeywordsBatch/NER.txt";
        output = new StringBuilder();
        for(String s : batchKeywordsByNER)
            output.append(s).append("\r\n");
        Files.write(Paths.get(outputFileName), output.toString().getBytes());
    }


    private static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    private static String writeFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}
