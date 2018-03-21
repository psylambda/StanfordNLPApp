import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

//import sun.java2d.pipe.OutlineTextRenderer;

public class FileOperation {

    private static final String Finally = null;

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        retrieveDict("C:\\Users\\wangy\\Desktop\\txt\\origin.txt", "C:\\Users\\wangy\\Desktop\\txt\\output.txt");
    }


    //

    /**
     * 对词典文件进行处理，词典文件每一个行都是一个词
     * 原词典文件可能含有括号，逗号等字符，
     * 简单的将特殊符号之后的字符去掉，以及将
     *
     * @param inputFileName  原词典文件名
     * @param outputFileName 新词典文件名
     * @throws IOException
     */
    public static void retrieveDict(String inputFileName, String outputFileName) throws IOException {
        // TODO Auto-generated method stub
        BufferedReader reader = null;
        FileOutputStream out = null;
        reader = new BufferedReader(new FileReader(inputFileName));
        out = new FileOutputStream(new File(outputFileName));
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = retrieveDict(line);
            System.out.println(line);
            System.out.println();
            System.out.println();
            if (line != null)
                out.write(line.getBytes());
            line = reader.readLine();
        }
        out.flush();
        reader.close();
        out.close();
    }

    public static String retrieveDict(String line) {
        // TODO Auto-generated method stub
        if (line == null || line.length() <= 2)
            return null;
        int end = line.length();
        if (line.indexOf(' ') >= 0) end = Math.min(end, line.indexOf(' '));
        if (line.indexOf('(') >= 0) end = Math.min(end, line.indexOf('('));
        if (line.indexOf('（') >= 0) end = Math.min(end, line.indexOf('（'));
        if (line.indexOf('[') >= 0) end = Math.min(end, line.indexOf('['));
        if (line.indexOf('：') >= 0) end = Math.min(end, line.indexOf('：'));
        if (line.indexOf('：') >= 0) end = Math.min(end, line.indexOf('：'));
        if (line.indexOf('）') >= 0) end = Math.min(end, line.indexOf('）'));
        if (line.indexOf('、') >= 0) end = Math.min(end, line.indexOf('、'));
        if (end <= 2) return null;
        return line.substring(0, end) + "\r\n";
    }
}
