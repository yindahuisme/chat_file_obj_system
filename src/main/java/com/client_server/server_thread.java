package com.client_server;

import com.data.woss_data;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class server_thread {
    //申明serversocket
    private ServerSocket server;
    //字符串模式暂存字符
    private StringBuilder str=new StringBuilder();
    //单例模式
    private ftp ftpinstance;
    //hashmap存储客户端
    private HashMap<String,Socket> allserverthreads=new HashMap<String, Socket>();
    //服务器解析状态
    //0：字符串状态
    //1：对象流状态
    //3：文件流状态
    private int analyze_state=0;
//服务器开关
    private boolean server_switch=true;
    private ArrayList<String> date_assembling=new ArrayList<String>();

    //访问器

    public ArrayList<String> getDate_assembling() {
        return date_assembling;
    }

    public void setDate_assembling(ArrayList<String> date_assembling) {
        this.date_assembling = date_assembling;
    }

    public int getAnalyze_state() {
        return analyze_state;
    }

    public void setAnalyze_state(int analyze_state) {
        this.analyze_state = analyze_state;
    }

    public HashMap<String, Socket> getAllserverthreads() {
        return allserverthreads;
    }

    public void setAllserverthreads(HashMap<String, Socket> allserverthreads) {
        this.allserverthreads = allserverthreads;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public StringBuilder getStr() {
        return str;
    }

    public void setStr(StringBuilder str) {
        this.str = str;
    }

    //构造器
    public server_thread(final int port) throws IOException {
        ftpinstance=ftp.getinstance();
        //服务器开启标识
        //System.out.println("connected_server_checkbox   "+ftpinstance.getConnected_server_checkbox().isSelected());

        ftpinstance.getConnected_server_checkbox().setSelected(true);

        //  System.out.println("connected_server_checkbox   "+ftpinstance.getConnected_server_checkbox().isSelected());
        try {
            setServer(new ServerSocket(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //服务器接收套接字线程开启
        new Thread(new Runnable(){

            public void run() {
                while (true) {
                    try {
                        //开始监听
                        Socket client = getServer().accept();
                        //开启服务线程
                        int n=0;
                        while(allserverthreads.containsKey(client.getInetAddress().toString()+"@"+n))
                        {
                            n++;
                        }
                        Thread serverthread= new serverthread(client.getInetAddress().toString()+"@"+n,client);
                        serverthread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }}
        }).start();
    }
    //server消息信息解析
    public  void analyze_massages_server(byte[] stream,String massage,String client_name,Socket client,OutputStream out,InputStream read)
    {
        System.out.println("服务端开始解析消息");
        System.out.println("massage:"+massage);
        if(massage.startsWith("@obj:"))
        {
            //进入对象流状态
            setAnalyze_state(1);
            ftpinstance.getConnected_server_checkbox().setText("对象模式");
            return;
        }
        if(massage.startsWith("@file:"))
        {
            //进入文件流状态
            setAnalyze_state(2);
            ftpinstance.getConnected_server_checkbox().setText("文件模式");
            return;
        }
        if(massage.startsWith("@pwd:"))
        {
            System.out.println("验证密码");
            //验证密码

            String sub=massage.substring(5).trim();
            System.out.println("客户端密码:"+sub+":"+sub.length());
            System.out.println("服务端密码:"+ftp.getinstance().getConnected_pwd_field().getText()+":"+ftp.getinstance(). getConnected_pwd_field().getText().length());
            if(sub.equals(ftp.getinstance().getConnected_pwd_field().getText()))
            {
                System.out.println("密码正确");
                //加入线程集
                int n=0;
                while(allserverthreads.containsKey(client.getInetAddress().toString()+"@"+n))
                {
                    n++;
                }
                getAllserverthreads().put(client.getInetAddress().toString()+"@"+n,client);
                //密码正确
                try {
                    ftp.getinstance().getServer_thread().senddata("@pwdcorrect:",client.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //连接成功
                ftp.getinstance().connect_successful_event();

                //改变连接状态
                ftp.getinstance().getConnected_link_checkbox().setSelected(true);
                ftp.getinstance().getConnected_link_checkbox().setText("连接状态:"+allserverthreads.size());
                ftp.getinstance().getConnect_connect_button().setEnabled(false);
                ftp.getinstance().getSendpage().setText("群发消息");
                //打印消息
                ftp.getinstance().console_log_textarea_append(client.getInetAddress()+":连接成功");
                //刷新panel面板
                ftp.getinstance().panel.updateUI();
            }
            else
            {
                System.out.println("密码错误");
                server_switch=false;


                //密码错误
                try {
                    ftp.getinstance().getServer_thread().senddata("@pwdwrong:",client.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //停止服务端线程
                try {
                    out.close();
                    read.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        //收到消息
        ftp.getinstance().console_log_textarea_append("收到"+client_name+"的消息:"+massage);
    }

    //更具输出流发送数据
    public void senddata(String massage,OutputStream out)
    {
        try {
            out.write(massage.trim().getBytes("gbk"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(massage.equals("@pwdwrong:"))
        ftp.getinstance().console_log_textarea_append("密码错误，拒绝连接");

    }

    //群发数据
    public void senddata(String massage)
    {
        //群发
        Iterator<Map.Entry<String, Socket>> iterator = allserverthreads.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Socket> entry = iterator.next();

            try {
                entry.getValue().getOutputStream().write(massage.trim().getBytes("gbk"));
                entry.getValue().getOutputStream().flush();
                ftp.getinstance().console_log_textarea_append("发送消息到"+entry.getKey()+":"+massage.trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("服务器发送数据");
    }
    //停止服务器
    public void stopserver()
    {
        for(Map.Entry<String,Socket> entry:allserverthreads.entrySet()) {
            try {
                entry.getValue().getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                entry.getValue().getOutputStream().flush();
                entry.getValue().getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                entry.getValue().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            getServer().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //内部类serverthread
    class serverthread extends Thread {
        //私有属性
        private String name_client;
        private Socket client;
        private InputStream read;
        private OutputStream out;

        //访问器


        public String getName_client() {
            return name_client;
        }


        public void setName_client(String name) {
            this.name_client = name;
        }

        public InputStream getRead() {
            return read;
        }

        public void setRead(InputStream read) {
            this.read = read;
        }

        public OutputStream getOut() {
            return out;
        }

        public void setOut(OutputStream out) {
            this.out = out;
        }

        public Socket getClient() {
            return client;
        }

        public void setClient(Socket client) {
            this.client = client;
        }

        public serverthread(String name, Socket client) {
            setName_client(name);
            setClient(client);
        }

        //线程方法
        @Override
        public void run() {
            //打印连接
            System.out.println("客户端连接");

            //获得客户端输出流
            try {
                out = new BufferedOutputStream(getClient().getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                read = new BufferedInputStream(getClient().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //循环响应
            server_switch=true;
            while (server_switch) {
                System.out.println("根据状态选择模式");
                //消息模式
                if (getAnalyze_state() == 0) {
                    //暂存读到的字节数组长度
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    try {

                        while ((len = read.read(buffer)) != -1) {
                            System.out.println("接受到消息");
                            getStr().delete(0, getStr().length());
                            getStr().append(new String(buffer, 0, buffer.length, "gbk").trim());

                            //执行操作
                            analyze_massages_server(buffer, getStr().toString(), getName_client(), getClient(),out,read);
                            buffer = new byte[1024];
                            if(getAnalyze_state()!=0||!server_switch)
                                break;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //对象模式
                if (getAnalyze_state() == 1) {
                //对象模式接收
                    System.out.println("服务器对象模式");
                    while (read!=null) {
                        //接收数据
                        //先进行gbk解码前60个字节；
                        byte[] protocol = new byte[60];
                        try {
                            getRead().read(protocol);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //解析协议
                        String pro = null;
                        try {
                            pro = new String(protocol, 0, protocol.length, "gbk");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println("文件协议头："+pro);
                        if (pro.startsWith("@massage:")) {
                            //进入消息模式
                            setAnalyze_state(0);
                            ftpinstance.getConnected_server_checkbox().setText("消息模式");
                            break;
                        }
                        if (pro.startsWith("@file:")) {
                            //进入文件模式
                            setAnalyze_state(2);
                            ftpinstance.getConnected_server_checkbox().setText("文件模式");
                            break;
                        }
                        if (pro.startsWith("@woss:")) {
                            //开始接收文件  打印接收日志
                            ftp.getinstance().console_log_textarea_append("开始接收" + getName_client() + "的woss.log文件");
                            ObjectInputStream objectInputStream = null;
//                            try {
//                                objectInputStream = new ObjectInputStream(read);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            woss_data obj = null;

                            //是否传输完成
                            boolean complete=false;
                           while (!complete&&read!=null){
                            try {
                                int len=0;
                                byte[] buffer=new byte[200];
                                while ((len=read.read(buffer))!=-1){
//                                while ((obj = (woss_data) objectInputStream.readObject()) != null) {

                                objectInputStream=new ObjectInputStream(read);
                                    try {
                                        obj = (woss_data) objectInputStream.readObject();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    //判断是否传输完成
                                    if(obj.getUsername().equals("传输完成对象"))
                                    {
                                        complete=true;
                                        break;
                                    }

                                    //得到对象
                                    String date = obj.getOnline();
                                    System.out.println("上线时间：" + date);
                                    Pattern pattern1 = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})\\s(\\d{1,2}):(\\d{1,2}):(\\d{1,2})");
                                    Matcher matcher1 = pattern1.matcher(date);
                                    matcher1.find();
                                    //根据年月日创建文件夹及文件名
                                    //得到保存路径
                                    String save_path = ftp.getinstance().getConnected_savefile_filechooser().getText();
                                    String path = "";
                                    //年
                                    path = save_path + File.separator + "year" + matcher1.group(1);
                                    if (date_assembling.contains(matcher1.group(1))) {
                                        //如果存在该年文件夹，存日志
//                                        FileOutputStream outputStream = new FileOutputStream(new File(path + File.separator + obj.getUsername() + ".log"));
//                                        //日志信息
//                                        String log = "用户名：" + obj.getUsername() + "\n" + "服务器名：" + obj.getProtocol_address() + "\n" + "上线时间：" + obj.getOnline() + "\n" + "持续时间：" + obj.getOnline_time() + "s" + "\n" + "用户ip：" + obj.getIp() + "\n";
//                                        outputStream.write(log.getBytes());

                                    } else {
                                        //创建文件夹

                                        new File(path).mkdir();
                                        //加入集合
                                        date_assembling.add(matcher1.group(1));
                                    }
                                    //月
                                    path = save_path + File.separator + "year" + matcher1.group(1) + File.separator + "mon" + matcher1.group(2);
                                    if (date_assembling.contains(matcher1.group(1) + "-" + matcher1.group(2))) {
                                        //如果存在该月文件夹，存日志
//                                        FileOutputStream outputStream = new FileOutputStream(new File(path + File.separator + obj.getUsername() + ".log"));
//                                        //日志信息
//                                        String log = "用户名：" + obj.getUsername() + "\n" + "服务器名：" + obj.getProtocol_address() + "\n" + "上线时间：" + obj.getOnline() + "\n" + "持续时间：" + obj.getOnline_time() + "s" + "\n" + "用户ip：" + obj.getIp() + "\n";
//                                        outputStream.write(log.getBytes());
                                    } else {
                                        //创建文件夹
                                        new File(path).mkdir();
                                        //加入集合
                                        date_assembling.add(matcher1.group(1) + "-" + matcher1.group(2));
                                    }
                                    //日
                                    path = save_path + File.separator + "year" + matcher1.group(1) + File.separator + "mon" + matcher1.group(2) + File.separator + "day" + matcher1.group(3);
                                    if (date_assembling.contains(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3))) {
                                        //如果存在该日文件夹，存日志
//                                        FileOutputStream outputStream = new FileOutputStream(new File(path + File.separator + obj.getUsername() + ".log"));
//                                        //日志信息
//                                        String log = "用户名：" + obj.getUsername() + "\n" + "服务器名：" + obj.getProtocol_address() + "\n" + "上线时间：" + obj.getOnline() + "\n" + "持续时间：" + obj.getOnline_time() + "s" + "\n" + "用户ip：" + obj.getIp() + "\n";
//                                        outputStream.write(log.getBytes());
                                    } else {
                                        //创建文件夹
                                        new File(path).mkdir();
                                        //加入集合
                                        date_assembling.add(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3));
                                    }
                                    //时
                                    path = save_path + File.separator + "year" + matcher1.group(1) + File.separator + "mon" + matcher1.group(2) + File.separator + "day" + matcher1.group(3) + File.separator + "hour" + matcher1.group(4);
                                    if (date_assembling.contains(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3) + "_" + matcher1.group(4))) {
                                        //如果存在该时文件夹，存日志
//                                        FileOutputStream outputStream = new FileOutputStream(new File(path + File.separator + obj.getUsername() + ".log"));
//                                        //日志信息
//                                        String log = "用户名：" + obj.getUsername() + "\n" + "服务器名：" + obj.getProtocol_address() + "\n" + "上线时间：" + obj.getOnline() + "\n" + "持续时间：" + obj.getOnline_time() + "s" + "\n" + "用户ip：" + obj.getIp() + "\n";
//                                        outputStream.write(log.getBytes());
                                    } else {
                                        //创建文件夹
                                        new File(path).mkdir();
                                        //加入集合
                                        date_assembling.add(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3) + "_" + matcher1.group(4));
                                    }
                                    //分
                                    path = save_path + File.separator + "year" + matcher1.group(1) + File.separator + "mon" + matcher1.group(2) + File.separator + "day" + matcher1.group(3) + File.separator + "hour" + matcher1.group(4) + File.separator + "min" + matcher1.group(5);
                                    if (date_assembling.contains(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3) + "_" + matcher1.group(4) + ":" + matcher1.group(5))) {
                                        //如果存在该分文件夹，存日志

                                    } else {
                                        //创建文件夹
                                        new File(path).mkdir();

                                        //加入集合
                                        date_assembling.add(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3) + "_" + matcher1.group(4) + ":" + matcher1.group(5));
                                    }
                                    //秒
                                    path = save_path + File.separator + "year" + matcher1.group(1) + File.separator + "mon" + matcher1.group(2) + File.separator + "day" + matcher1.group(3) + File.separator + "hour" + matcher1.group(4) + File.separator + "min" + matcher1.group(5)+ File.separator + "sec" + matcher1.group(6);
                                    if (date_assembling.contains(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3) + "_" + matcher1.group(4) + ":" + matcher1.group(5)+":"+matcher1.group(6))) {
                                        //如果存在该分文件夹，存日志

                                    } else {
                                        //创建文件夹
                                        new File(path).mkdir();

                                        //加入集合
                                        date_assembling.add(matcher1.group(1) + "-" + matcher1.group(2) + "-" + matcher1.group(3) + "_" + matcher1.group(4) + ":" + matcher1.group(5)+":"+matcher1.group(6));
                                    }
                                    FileOutputStream outputStream = new FileOutputStream(new File(path + File.separator + obj.getUsername() + ".log"));
                                    //日志信息
                                    String log = "用户名：" + obj.getUsername() + "\n" + "服务器名：" + obj.getProtocol_address() + "\n" + "上线时间：" + obj.getOnline() + "\n" + "持续时间：" + obj.getOnline_time() + "s" + "\n" + "用户ip：" + obj.getIp() + "\n";
                                    outputStream.write(log.getBytes());

                                }
                            } catch(StreamCorruptedException e){

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            catch (ClassNotFoundException e) {
//                                e.printStackTrace();
//                            }


                        }
                            //打印文件传输完成
                            ftp.getinstance().console_log_textarea_append("接收完成");

                            //跳出文件传输
                            if(getAnalyze_state()!=1)
                                break;

                        }
                    }
                }
                //文件流模式
                if (getAnalyze_state() == 2) {
                    System.out.println("服务器文件模式");
                    //存储路径
                    String save_directory=ftp.getinstance().getConnected_savefile_filechooser().getText();
                    System.out.println("存储路径："+save_directory);
                    while (read!=null) {
                        //接收数据
                        //先进行gbk解码前60个字节；
                        byte[] protocol = new byte[60];
                        try {
                            getRead().read(protocol);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //解析协议
                        String pro = null;
                        try {
                            pro = new String(protocol, 0, protocol.length, "gbk");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println("文件协议头："+pro);
                        if (pro.startsWith("@massage:")) {
                            //进入消息模式
                            setAnalyze_state(0);
                            ftpinstance.getConnected_server_checkbox().setText("消息模式");
                            break;
                        }
                        if (pro.startsWith("@obj:")) {
                            //进入对象模式
                            setAnalyze_state(1);
                            ftpinstance.getConnected_server_checkbox().setText("对象模式");
                            break;
                        }
                        if (pro.startsWith("@file:")) {
                            System.out.println("开始切割协议后缀");
                            //切割出文件后缀
                            String tail=pro.substring(6).trim();
                            System.out.println("tail:"+tail);
                            //根据文件路径以及文件名打开流
                            FileOutputStream os=null;

                            try {
                                os=new FileOutputStream(new File(ftp.getinstance().getConnected_savefile_filechooser().getText()+tail));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            System.out.println("保存路径："+ftp.getinstance().getConnected_savefile_filechooser().getText()+tail);
                            //开始接收文件  打印接收日志
                            ftp.getinstance().console_log_textarea_append("开始接收"+getName_client()+"的文件");


                            byte[] buffer = new byte[1024];
                            int len =0;

                            try {
                                while ((len=read.read(buffer))!=-1)
                                {
                                    System.out.println("收到文件数据部分");
                                    os.write(buffer, 0, len);    //写入指定地方
                                    System.out.println("收到长度："+len);
                                    if(len==1024)
                                    continue;
                                    else
                                        break;
                                }
                            }catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            //打印文件传输完成
                            ftp.getinstance().console_log_textarea_append("接收完成");

                            //跳出文件传输
                            if(getAnalyze_state()!=2)
                            break;

                        }
                    }
                }
            }
        }
    }
}



