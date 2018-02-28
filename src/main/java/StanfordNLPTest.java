import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class StanfordNLPTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//FileTest();
		MyTest();
		//chineseTest();
		//newChineseTest();
		//englishTest();
	}
	public static void englishTest()
	{
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
        // NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = "She went to America last week.";// Add your text here!
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);



        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and
        // has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        List<String> words = new ArrayList<>();
        List<String> posTags = new ArrayList<>();
        List<String> nerTags = new ArrayList<>();
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                words.add(word);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);
                posTags.add(pos);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                nerTags.add(ne);
            }
        }

        System.out.println(words.toString());
        System.out.println(posTags.toString());
        System.out.println(nerTags.toString());
	}
	public static void newChineseTest() throws IOException
	{
		String text = "克林顿说，华盛顿将逐步落实对韩国的经济援助。"
		        + "金大中对克林顿的讲话报以掌声：克林顿总统在会谈中重申，他坚定地支持韩国摆脱经济危机。";
		Annotation document = new Annotation(text);
		// Setup Chinese Properties by loading them from classpath resources
		//Properties props = new Properties();
		//props.load(IOUtils.readerFromString("StanfordCoreNLP-chinese.properties"));
		// Or this way of doing it also works
		Properties props = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-chinese.properties"});
		StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
		corenlp.annotate(document);

		corenlp.prettyPrint(document, System.out);
	}
	
	public static void chineseTest()
	{
		String text = "She went to America last week.";
		Annotation document = new Annotation(text);
		StanfordCoreNLP corenlp = new StanfordCoreNLP("MyStanfordNLPChinese.properties");
		corenlp.annotate(document);

		corenlp.prettyPrint(document, System.out);
	}
	
	public static void MyTest()
	{
		String text = "我感冒了，想喝口服液，感冒是一种疾病，常伴有头痛和发烧。";
		Annotation document = new Annotation(text);
		StanfordCoreNLP corenlp = new StanfordCoreNLP("test.properties");
		corenlp.annotate(document);

		corenlp.prettyPrint(document, System.out);
	}
	
	
	public static void FileTest() throws IOException
	{
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw  
            /* 读入TXT文件 */  
            String pathname = "D:\\NLP\\TrainNERChinese\\无关小说.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径  
            BufferedReader  reader = new BufferedReader(new FileReader(pathname));
            String line = "";  
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            line = reader.readLine();  
            while (line != null) {  
                System.out.println(line);
                sb.append(line + "\n");//将读取的字符串添加换行符后累加存放在缓存中
                line = reader.readLine(); // 一次读入一行数据  
            }  
            String str = sb.toString();
            System.out.println(str);
        	Annotation document = new Annotation(str);
    		StanfordCoreNLP corenlp = new StanfordCoreNLP("ChineseTest.properties");
    		corenlp.annotate(document);
    		
    		FileOutputStream out = null;
    		out = new FileOutputStream(new File("D:\\NLP\\TrainNERChinese\\无关小说分词.txt"));
    		// these are all the sentences in this document
            // a CoreMap is essentially a Map that uses class objects as keys and
            // has values with custom types
            List<CoreMap> sentences = document.get(SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(TextAnnotation.class);
                    out.write((word+"\tO\r\n").getBytes());
                }
            }
            out.flush(); // 把缓存区内容压入文件  
            out.close(); // 最后记得关闭文件  
            reader.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
}
