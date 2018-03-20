import java.io.*;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordNLPWrapper {
	public static Annotation AnalyzeFromString(String propertyFileName, String text)
	{
		Annotation document = new Annotation(text);
		StanfordCoreNLP corenlp = new StanfordCoreNLP(propertyFileName);
		corenlp.annotate(document);
		
		//corenlp.prettyPrint(document, System.out);
		return document;
	}
	
	public static Annotation AnalyzeFromFile(String propertyFileName, String InputFilename) throws IOException
	{
			BufferedReader  reader = new BufferedReader(new FileReader(InputFilename));
			String line = "";  
			StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
			line = reader.readLine();  
			while (line != null) {  
				System.out.println(line);
				sb.append(line + "\n");//将读取的字符串添加换行符后累加存放在缓存中
				line = reader.readLine(); // 一次读入一行数据  
			}  
			String str = sb.toString();
			reader.close();
			return AnalyzeFromString(propertyFileName, str);
	}
		
		
		
}
