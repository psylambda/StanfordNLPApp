import java.io.*;

public class WriteSampleData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileOutputStream out = null;
        FileWriter fw = null;

        int count = 1000;//写文件行数

        try {
            //经过测试：FileOutputStream执行耗时:17，6，10 毫秒
            out = new FileOutputStream(new File("D:\\NLP\\TrainNERChinese\\医疗小数据.txt"));
            long begin = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                out.write("感冒\tDIS\r\n".getBytes());
            }
            for (int i = 0; i < count; i++) {
                out.write("肺炎\tDIS\r\n".getBytes());
            }
            for (int i = 0; i < count; i++) {
                out.write("低烧\tSYM\r\n".getBytes());
            }
            for (int i = 0; i < count; i++) {
                out.write("头痛\tSYM\r\n".getBytes());
            }
            for (int i = 0; i < count; i++) {
                out.write("胶囊\tDRU\r\n".getBytes());
            }
            for (int i = 0; i < count; i++) {
                out.write("口服液\tDRU\r\n".getBytes());
            }
            
            out.close();
            long end = System.currentTimeMillis();
            System.out.println("FileOutputStream执行耗时:" + (end - begin) + " 毫秒");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}
