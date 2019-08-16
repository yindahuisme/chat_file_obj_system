package com.client_server;

import com.data.woss_data;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class client_thread {
    //属性
    private Socket client;
    //输入输出流
    private OutputStream write;
    private InputStream read;
    private static BufferedInputStream is;
    //声明发送文件
    private static File file=null;
    //字节流计数
    private static double num=0;
    //客户端只有消息模式：字符串暂存
    private StringBuilder str=new StringBuilder();
    //响应服务器消息
    private Thread clientthread;
    //客户端发送数据三种状态
    //0:消息模式
    //1：对象模式
    //2:文件流模式
    private int send_state=0;
    //转换两位小数
    DecimalFormat decimalFormat=new DecimalFormat("#.00");
    //用hashmap 保存解析结果对象
    HashMap<String, woss_data> wossdatas=new HashMap<String, woss_data>();
    //解析行数
    private int row_num;

    //防止对象处理时阻塞frame，加一个对象处理线程
    private  obj_thread obj_thread;

    //客户端开关
    private boolean client_switch=true;
    //访问器


    public client_thread.obj_thread getObj_thread() {
        return obj_thread;
    }

    public void setObj_thread(client_thread.obj_thread obj_thread) {
        this.obj_thread = obj_thread;
    }

    public int getSend_state() {
        return send_state;
    }

    public void setSend_state(int send_state) {
        this.send_state = send_state;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public StringBuilder getStr() {
        return str;
    }

    public void setStr(StringBuilder str) {
        this.str = str;
    }

    //构造器
    public client_thread(String ip, int port) {


        try {
            setClient(new Socket(ip,port));

        } catch (IOException e) {
                e.printStackTrace();
            System.out.println("连接服务器失败");
        }

//        try
//        {
//            ftp.getinstance().getClient_socket() .connect(new InetSocketAddress(ip,port));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            write=getClient().getOutputStream();
            read=getClient().getInputStream();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //接受服务器消息
        clientthread=new Thread(
                new Runnable() {
                    public void run() {
                        int len=0;
                        byte[] buffer=new byte[1024];

                        System.out.println("客户端开始接收消息");

                        try {

                                while ((len = read.read(buffer)) != -1) {
                                    System.out.println("接收到消息");
                                    getStr().delete(0, getStr().length());
                                    getStr().append(new String(buffer, 0, buffer.length, "gbk").trim());
                                    buffer = new byte[1024];
                                    //执行操作
                                    analyze_massages_client(getStr().toString());
                                    if(!client_switch)
                                        break;
                                }

                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
        );
        clientthread.start();
    }

    //发送数据
    public  void senddata(String str) throws IOException
    {

        //消息模式
        if(getSend_state()==0) {
            write.write(str.trim().getBytes("gbk"));
            write.flush();
            ftp.getinstance().console_log_textarea_append("发出消息："+str.trim());
            //如果发送"@obj:",进入对象模式
            if (str.equals("@obj:")) {
                setSend_state(1);
                ftp.getinstance().console_log_textarea_append("进入对象模式");
                return;
            }
            //如果发送"@file:",进入文件模式
            if (str.equals("@file:")) {
                setSend_state(2);
                ftp.getinstance().console_log_textarea_append("进入文件模式");
                return;
            }
        }
        //对象模式
        if(getSend_state()==1) {
            ftp.getinstance().console_log_textarea_append("发出消息：" + str.trim());
            //如果发送"@massage:",进入消息模式
            if (str.equals("@massage:")) {
                //定义60个字节的协议字符
                byte[] protocol=new byte[60];
                protocol=str.getBytes("gbk");
                write.write(protocol);
                write.flush();

                setSend_state(1);
                ftp.getinstance().console_log_textarea_append("进入对象模式");
                return;
            }
            //如果发送"@file:",进入文件模式
            if (str.equals("@file:")) {
                //定义60个字节的协议字符
                byte[] protocol=new byte[60];
                protocol=str.getBytes("gbk");
                write.write(protocol);
                write.flush();

                setSend_state(2);
                ftp.getinstance().console_log_textarea_append("进入文件模式");
                return;
            }
            //如果发送"@woss:",进入woss模式
            if (str.startsWith("@woss:")) {
                //开启对象处理线程
             obj_thread=new obj_thread(str);
             obj_thread.start();

            }
        }
        //文件模式
        if(getSend_state()==2)
        {
            //进入消息模式
            if(str.equals("@massage:"))
            {
                //定义60个字节的协议字符
                byte[] protocol=new byte[60];
                protocol=str.getBytes("gbk");
                write.write(protocol);
                write.flush();
                //改变状态
                setSend_state(0);
                ftp.getinstance().console_log_textarea_append("进入消息模式");
                return;
            }
            //进入对象模式
            if(str.equals("@obj:"))
            {
                //定义60个字节的协议字符
                byte[] protocol=new byte[60];
                protocol=str.getBytes("gbk");
                write.write(protocol);
                write.flush();
                //改变状态
                setSend_state(1);
                ftp.getinstance().console_log_textarea_append("进入对象模式");
            return;
            }
            //发送文件
            StringTokenizer tokenizer=new StringTokenizer(str,";");
            while (tokenizer.hasMoreElements())
            {
                String filename=tokenizer.nextToken();
               file=new File(filename);
               final FileInputStream inputStream=new FileInputStream(file);
               is=new BufferedInputStream(inputStream);

                //协议字符数组,发过去
                byte[] protocol=new byte[60];
                //解析出文件后缀
                Pattern pattern=Pattern.compile("\\\\([^\\\\]+\\.\\S+)$");
                Matcher matcher=pattern.matcher(filename);
                if(matcher.find())
                protocol=("@file:"+matcher.group(1)).getBytes("gbk");
                write.write(protocol);
                write.flush();
                new Thread(new Runnable() {
                    public void run() {
                        //开始传送
                        byte[] buffer = new byte[1024];
                        int len=0;
                        //打印开始发送
                        ftp.getinstance().console_log_textarea_append("开始发送文件：");

                        int start = (int)System.currentTimeMillis();
                        try {
                        while ((len = is.read(buffer)) != -1) {

                                write.write(buffer, 0, len);

                            num+=len;
                            ftp.getinstance().console_log_textarea_append("发送中:"+decimalFormat.format((num/file.length())*100)+"%");

                        }} catch (IOException e) {
                        e.printStackTrace();
                    }
                        //输出流刷出
                        try {
                            write.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int end = (int)System.currentTimeMillis();
                        ftp.getinstance().console_log_textarea_append("发送花费时间：" + (int)((end-start)/1000)+"s");

                        num=0;
                        //关闭文件流
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        }
    }

    //client消息信息解析
    public void analyze_massages_client(String massage)
    {
        System.out.println("客户端开始解析消息");

        //密码错误
        if(massage.startsWith("@pwdwrong:"))
        {
            try {
                client_switch=false;
                close_client();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //打印消息
            ftp.getinstance(). console_log_textarea_append(new Date()+":密码错误");
            return;
        }
        if(massage.startsWith("@pwdcorrect:"))
        {
            //连接成功
            ftp.getinstance().connect_successful_event();

            //改变连接状态
            ftp.getinstance().getConnected_link_checkbox().setSelected(true);
            ftp.getinstance().getConnect_connect_button().setEnabled(false);
            ftp.getinstance().getSendpage().setText(ftp.getinstance().getConnect_ip_field().getText());
            //打印消息

            ftp.getinstance().console_log_textarea_append(":成功连接到服务器"+ftp.getinstance().getConnect_ip_field().getText());
            //刷新panel面板
            ftp.getinstance().panel.updateUI();
            return;
        }
        //打印消息

        ftp.getinstance().console_log_textarea_append("收到"+ftp.getinstance().getConnect_ip_field().getText()+"的消息:"+massage);
    }
    //关闭客户端
   public void close_client() throws IOException {
        write.flush();
        write.close();
        read.close();
       client.close();
    }

    //内部类，对象处理线程
    class obj_thread extends Thread
    {
        //发送的内容
        private String str;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        //构造函数
       public obj_thread(String massage)
       {
           //初始化属性
          setStr(massage);

       }
        @Override
        public void run() {
            //定义60个字节的协议字符
            byte[] protocol=new byte[60];
            try {
                protocol="@woss:".getBytes("gbk");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                write.write(protocol);
                write.flush();
            }catch(IOException e){
                e.printStackTrace();
            }
            System.out.println("开始切割协议后缀");
            //切割出文件后缀
            String tail=str.substring(6).trim();


            //解析woss日志信息
            File resource = new File(tail);
            //获取行数
            LineNumberReader lnr = null;
            try {
                lnr = new LineNumberReader(new FileReader(resource));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                lnr.skip(Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int lineNo = lnr.getLineNumber() + 1;

            //打印日志
            ftp.getinstance().console_log_textarea_append("开始将" + tail + "解析成对象。。。");
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(new FileInputStream(resource), "gbk");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            BufferedReader buffered = new BufferedReader(reader);
            //暂存行
            String row = "";
            Pattern pattern = Pattern.compile("#(\\w{0,13})\\|(\\S{13})\\|(\\d{1})\\|(\\d{8,12})\\|(\\d+.\\d+.\\d+.\\d+)");
            try {
                while ((row = buffered.readLine()) != null) {


                    //开始解析行
                    Matcher matcher = pattern.matcher(row);
                    //编译结果
                    matcher.find();

                    woss_data obj = null;
                    obj = wossdatas.get(matcher.group(5));
                    if (obj != null && matcher.group(3).equals("8")) {
                        //存在用户，下线，构成完整记录
                        obj.setOnline_time(Integer.valueOf(matcher.group(4)) - Integer.valueOf(obj.getOnline_time_stamp()));
                        //发送对象

                        ObjectOutputStream objectOutputStream=new ObjectOutputStream(write);
                        objectOutputStream.writeObject(obj);
                        objectOutputStream.flush();

                        write.flush();

                        //解析行数累加
                        row_num++;
                        ftp.getinstance().console_log_textarea_append("解析进度："+new DecimalFormat("#.00").format(((double)row_num/lineNo)*100)+"%");

                        //移除记录
                        System.out.println("移除："+wossdatas.size());
                        wossdatas.remove(matcher.group(5));
                        continue;
                    }
                    if(matcher.group(3).equals("7")){
                        //存入map
                        System.out.println("存入："+wossdatas.size());
                        woss_data object = new woss_data(matcher.group(1), matcher.group(2), ftp.stampToDate(matcher.group(4)), matcher.group(5), matcher.group(4));
                        wossdatas.put(matcher.group(5), object);
                        //解析行数累加
                        row_num++;
                        ftp.getinstance().console_log_textarea_append("解析进度："+new DecimalFormat("#.00").format(((double)row_num/lineNo)*100)+"%");

                    }
                    //debug解析进度
//                    System.out.println("解析进度："+new DecimalFormat("#.00").format(((double)row_num/lineNo)*100)+"%");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            //解析完成
            //发送传输完成对象
            woss_data complete_obj=new woss_data("传输完成对象","","","","");
            //发送对象
            try {
                write.flush();

                ObjectOutputStream objectOutputStream=new ObjectOutputStream(write);
                objectOutputStream.writeObject(complete_obj);
                objectOutputStream.flush();
            }catch (IOException e){
                e.printStackTrace();
            }


            ftp.getinstance().console_log_textarea_append("解析woss.log文件成功，已发往服务器");

        }
    }
}


