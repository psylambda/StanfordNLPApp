package team.intelligenthealthcare.keywordsextraction;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DictTest {

    @Test
    public void testAhocorasick() throws IOException {
        Properties property = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("keywordsExtraction.properties");
        property.load(is);
        String str = property.getProperty("corpusGeneration.dict");
        Map<String, String> m = JSON.parseObject(str, Map.class);
        Dict dict = new Dict(m);
        List<List<String>> res = dict.ahocorasickParse("临床上以反复发作的咳嗽、咳痰或伴有喘鸣音为特征。");
            for (List<String> j : res) {
                for (String k : j)
                    System.out.print(k + "\t");
                System.out.print("\n");
            }
            System.out.print("\n");
    }
}
