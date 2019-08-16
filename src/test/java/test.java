import org.junit.Test;

import javax.annotation.Resource;
import javax.swing.text.DateFormatter;
import java.io.*;
import java.lang.reflect.Field;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    /*
    /yindahu
    2019.8.16
     */
    @Test
    public void test()
    {
//        String date="2018-01-02 20:05:03";
//String file_url="#|037:wKgB1660A|8|1285376779|176.25.20.110";
//String file_url1="#briup1292|037:wKgB1660A|7|1285376780|108.76.13.72";
//        Pattern pattern=Pattern.compile("#(\\w{0,13})\\|(\\S{13})\\|(\\d{1})\\|(\\d{8,12})\\|(\\d+.\\d+.\\d+.\\d+)");
//        Pattern pattern1=Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})\\s(\\d{1,2}):(\\d{1,2}):(\\d{1,2})");
//                Matcher matcher1=pattern1.matcher(date);
//        Matcher matcher=pattern.matcher(file_url);
//       if(matcher.find()) {
//           for (int i = 0; i <= 5; i++) {
//               System.out.println(matcher.group(i));
//           }
//       }

//        System.out.println(Integer.valueOf("1285376779")-1);


//        File resource =new File("E:\\test\\radwtmp_test.txt");
//        System.out.println(resource.length());
//        InputStreamReader reader=null;
//        try {
//            reader=new InputStreamReader(new FileInputStream(resource),"gbk");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        BufferedReader buffered=new BufferedReader(reader);
//        try {
//            System.out.println(buffered.readLine());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String date="2018-01-02 20:05:03";
//        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//           System.out.println(dateFormat.parse(date).getMonth());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


//        ArrayList<String> arrayList=new ArrayList<String>();
//        arrayList.add("jk");
//        System.out.println(arrayList.contains("jk"));


//反射
//            Class a=null;
//        try {
//
//            a = Class.forName("com.data.woss_data");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//           Field[] m= a.getFields();
//        for(Field mm:m)
//        {
//            System.out.println(mm.getName());
//        }



    }




public static void main(String[] argc)
    {
        System.out.println("w:4  m:7  n:20");
       System.out.println( "距离："+array_instance(4,7,20));
    }

    //蓝桥杯
    //矩阵距离问题
    /**
     * w 矩阵边长
     * m 序号1
     * n 序号2
     */
   static int array_instance(int w,int m,int n)
    {
        //m的位置
        //行
        int row_m;
        if(m%w!=0)
        row_m = m/w+1;
        else
            row_m=m/w;

        int row_n;
        if(n%w!=0)
        row_n = n/w+1;
        else
            row_n=n/w;
        //列
        int col_m;
        if(row_m%2==1)
       col_m = m%w;
        else col_m=w+1-m%w;

        int col_n;
        if(row_n%2==1)
            col_n = n%w;
        else col_n=w+1-n%w;
        //距离和
        return Math.abs(row_m-row_n)+Math.abs(col_m-col_n);
    }
}
