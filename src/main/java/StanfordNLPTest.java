import java.io.*;
import java.util.*;

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
		//generateTrainingText();
		generateNERResult();
		System.out.println("Done!");
	}

	public static void generateTrainingText() throws IOException {

		Map<String, String> dictFileNameToSymbol = new HashMap<String, String>() {
			{
				put("C:\\Users\\wangy\\git\\IntelligentHealthcare\\KeyWordsExtraction\\词典\\疾病词典.txt", "DISEASE");
				put("C:\\Users\\wangy\\git\\IntelligentHealthcare\\KeyWordsExtraction\\词典\\药品词典.txt", "DRUG");
				put("C:\\Users\\wangy\\git\\IntelligentHealthcare\\KeyWordsExtraction\\词典\\症状词典.txt", "SYMPTOM");
			}
		};
		generateTrainingText(dictFileNameToSymbol, "O",
				"C:\\Users\\wangy\\git\\IntelligentHealthcare\\KeyWordsExtraction\\语料库\\生语料库.txt",
				"C:\\Users\\wangy\\eclipse-workspace\\StanfordNLPApp\\NER\\input.txt");
	}

	public static void generateTrainingText(Map<String, String> dictFileNameToSymbol, String defaultSymbol,
			String corpusFileName, String targetFileName) throws IOException {
		// 获取词典
		Map<Set<String>, String> dictToSymbol = new HashMap<>();
		for (Map.Entry<String, String> dictFileNameAndSymbol : dictFileNameToSymbol.entrySet()) {
			String dictFileName = dictFileNameAndSymbol.getKey();
			String symbol = dictFileNameAndSymbol.getValue();
			Set<String> dict = new HashSet<>();
			BufferedReader reader = new BufferedReader(new FileReader(dictFileName));
			String word = reader.readLine();
			while (word != null && !word.isEmpty()) {
				dict.add(word);
				word = reader.readLine();
			}
			reader.close();
			dictToSymbol.put(dict, symbol);
		}
		// 获取目标文件
		FileOutputStream out = new FileOutputStream(new File(targetFileName));

		// 分词
		Annotation document = StanfordNLPWrapper.AnalyzeFromFile("ChineseSegmentation.properties", corpusFileName);
		List<String> words = new ArrayList<>();
		List<String> tags = new ArrayList<>();
		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			words.clear();
			tags.clear();
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				words.add(token.get(TextAnnotation.class));
				// 默认标签为"O"
				tags.add(defaultSymbol);
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < words.size(); i++) {
				sb.delete(0, sb.length());
				for (int j = i; j < words.size(); j++) {
					// sb包括i,i+1,...,j组成的复合词，检查是否在词典中
					sb.append(words.get(j));
					// 如果在词典中则打上标签
					for (Map.Entry<Set<String>, String> dictAndSymbol : dictToSymbol.entrySet()) {
						Set<String> dict = dictAndSymbol.getKey();
						String symbol = dictAndSymbol.getValue();
						if (dict.contains(sb.toString()))
							for (int k = i; k <= j; k++)
								if (tags.get(k).equals(defaultSymbol))
									tags.set(k, symbol);
					}

				}
			}
			for (int i = 0; i < words.size(); i++)
				out.write((words.get(i) + "\t" + tags.get(i) + "\r\n").getBytes());
		}
		out.flush(); // 把缓存区内容压入文件
		out.close();
	}

	public static void generateNERResult() throws IOException {
		generateNERResult("ner-model-SYM-DRUG.properties", "O", "D:\\NLP\\Medical\\Paragraphs.txt",
				"D:\\NLP\\Medical\\ParagraphsNER.txt", "D:\\NLP\\Medical\\*.txt");
	}

	public static void generateNERResult(String propertyFileName,  String defaultTag, String InputFilename, String targetFileName, String TagFileName) throws IOException
	{
		Annotation document = StanfordNLPWrapper.AnalyzeFromFile(propertyFileName, InputFilename);
		FileOutputStream out = new FileOutputStream(new File(targetFileName));
		
		Map<String, Set<String>> tagToWords = new HashMap<>();
		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			Map<String, List<String>> tagToWordsInSentence = new HashMap<>();
			StringBuilder sentenceBuilder = new StringBuilder();
			String lastTag = defaultTag;
			StringBuilder wordBuilder = new StringBuilder();
			
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String tag = token.get(NamedEntityTagAnnotation.class);
				sentenceBuilder.append(word);
				if(tag == lastTag)
					wordBuilder.append(word);
				else {
					if(!tagToWordsInSentence.containsKey(lastTag))
						tagToWordsInSentence.put(lastTag, new LinkedList<>());
					tagToWordsInSentence.get(lastTag).add(wordBuilder.toString());
					lastTag = tag;
					wordBuilder.replace(0, wordBuilder.length(),word);
				}
			}
			//fill last word
			if(wordBuilder.length() != 0)
			{
				if(!tagToWordsInSentence.containsKey(lastTag))
					tagToWordsInSentence.put(lastTag, new LinkedList<>());
				tagToWordsInSentence.get(lastTag).add(wordBuilder.toString());
			}
			sentenceBuilder.append("\n");
			//deal with keywords
			for(Map.Entry<String, List<String>> entry:tagToWordsInSentence.entrySet())
			{
				String tag = entry.getKey();
				List<String> words = entry.getValue();
				//skip the default tag
				if(tag.equals(defaultTag)) continue;
				//construct a line with a tag and corresponding words
				sentenceBuilder.append("* ").append(tag).append(" :");
				words.forEach(word->sentenceBuilder.append("  ").append(word));
				sentenceBuilder.append("\n");
				//insert the current words in the smallmap to the bigmap
				if(!tagToWords.containsKey(tag))
					tagToWords.put(tag, new HashSet<>());
				tagToWords.get(tag).addAll(words);
			}
			out.write(sentenceBuilder.append("\n").toString().getBytes());
		}
		out.flush(); 
		out.close();
		
		//write words with the same tag to a file
		for(Map.Entry<String, Set<String>> entry : tagToWords.entrySet())
		{
			String tag = entry.getKey();
			Set<String> words = entry.getValue();
			StringBuilder sb = new StringBuilder();
			words.forEach(s->sb.append(s).append("\n"));
			String curTagFileName = TagFileName.replace("*", tag);
			try(FileOutputStream curOut = new FileOutputStream(curTagFileName))
			{
				curOut.write(sb.toString().getBytes());
			} catch(IOException e)
			{
				 System.out.println("An I/O Exception Occurred");
			}
		}
	}

	// public static void generateNERResult(String propertyFileName, String
	// InputFilename, String targetFileName) throws IOException
	// {
	// Annotation document = StanfordNLPWrapper.AnalyzeFromFile(propertyFileName,
	// InputFilename);
	// FileOutputStream out = new FileOutputStream(new File(targetFileName));
	//
	// for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
	// StringBuilder sentenceBuilder = new StringBuilder();
	// List<String> list = new LinkedList<>();
	// String lastNerTag = "O";
	// StringBuilder keyWordsBuilder = new StringBuilder();
	//
	// for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
	// String word = token.get(TextAnnotation.class);
	// String curNerTag = token.get(NamedEntityTagAnnotation.class);
	// sentenceBuilder.append(word);
	// if(curNerTag == lastNerTag)
	// {
	// if(!curNerTag.equals("O"))
	// keyWordsBuilder.append(word);
	// } else {
	// if(!lastNerTag.equals("O"))
	// list.add(keyWordsBuilder.toString());
	// if(!curNerTag.equals("O"))
	// {
	// keyWordsBuilder.delete(0, keyWordsBuilder.length());
	// keyWordsBuilder.append(curNerTag+" "+word);
	// }
	// }
	// lastNerTag = curNerTag;
	// }
	// if(!lastNerTag.equals("O"))
	// list.add(keyWordsBuilder.toString());
	// keyWordsBuilder.delete(0, keyWordsBuilder.length());
	// for(String s : list)
	// keyWordsBuilder.append(" ["+s+"]");
	// String output =
	// sentenceBuilder.append('\t').append(keyWordsBuilder).append('\n').toString();
	// out.write(output.getBytes());
	// }
	// out.flush(); // 把缓存区内容压入文件
	// out.close();
	// }
}
