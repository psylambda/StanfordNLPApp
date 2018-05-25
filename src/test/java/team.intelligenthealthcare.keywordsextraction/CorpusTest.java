import com.alibaba.fastjson.JSON;
import org.junit.Test;
import team.intelligenthealthcare.keywordsextraction.Corpus;
import team.intelligenthealthcare.keywordsextraction.Dict;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CorpusTest {
    @Test
    public void testCorpus() throws IOException {
        Properties property = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(is);
        //read dict files
        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);

        //read corpus
        Corpus corpus = new Corpus(property.getProperty("corpusGeneration.propertyFileName"), dict, property.getProperty("corpusGeneration.defaultTag"));
        List<List<List<String>>> res = corpus.tagString("面骨骨质增生");
        for(List<List<String>> a : res) {
            for (List<String> b : a) {
                for (String c : b)
                    System.out.print(c + "\t");
                System.out.print("\n");
            }
        }

        System.out.print("\n");
    }
}
