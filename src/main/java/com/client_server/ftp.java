package com.client_server;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ftp extends JFrame
{
    //单例模式
    private static ftp ftp;
    //访问单例，加入同步锁，线程安全
    public static synchronized ftp getinstance(){
        if(ftp==null)
        {
            ftp=new ftp();
        }
        return ftp;
    }
    //属性
   private client_thread client_thread;
    private  server_thread server_thread;

    //主panel
    JPanel panel;
    JPanel connect_JPanel;
    JScrollPane console_JPanel;
    JPanel connected_JPanel;
    //屏幕分辨率
    int screenWidth=((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
    int screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
    //窗体控件
    // connect JPanel
    private JLabel connect_ip_lable;
    private JTextField connect_ip_field;
    private JLabel connect_port_lable;
    private JTextField connect_port_field;
    private JLabel connect_pwd_lable;
    public  JTextField connect_pwd_field;
    private JButton connect_connect_button;

    //connected JPanel
    private JCheckBox connected_link_checkbox;
    private JCheckBox connected_server_checkbox;
    private JLabel connected_port_lable;
    private JLabel connected_pwd_lable;
    private JTextField connected_port_field;
    private JTextField connected_pwd_field;
    private JButton connected_start_button;
    private JLabel connected_savefile_lable;
    private JTextField connected_savefile_filechooser;
    //console JPanel
    private JTextArea console_log_textarea;

    //connect successful JPanel
    private JLabel sendpage;
    private JLabel connect_successful_filepath_lable;
    private JTextField connect_successful_filepath_textfile;
    private JButton send;

    //访问器


    public JCheckBox getConnected_link_checkbox() {
        return connected_link_checkbox;
    }

    public void setConnected_link_checkbox(JCheckBox connected_link_checkbox) {
        this.connected_link_checkbox = connected_link_checkbox;
    }

    public com.client_server.client_thread getClient_thread() {
        return client_thread;
    }

    public void setClient_thread(com.client_server.client_thread client_thread) {
        this.client_thread = client_thread;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public JPanel getConnect_JPanel() {
        return connect_JPanel;
    }

    public void setConnect_JPanel(JPanel connect_JPanel) {
        this.connect_JPanel = connect_JPanel;
    }

    public JScrollPane getConsole_JPanel() {
        return console_JPanel;
    }

    public void setConsole_JPanel(JScrollPane console_JPanel) {
        this.console_JPanel = console_JPanel;
    }

    public JPanel getConnected_JPanel() {
        return connected_JPanel;
    }

    public void setConnected_JPanel(JPanel connected_JPanel) {
        this.connected_JPanel = connected_JPanel;
    }

    public JLabel getConnect_ip_lable() {
        return connect_ip_lable;
    }

    public void setConnect_ip_lable(JLabel connect_ip_lable) {
        this.connect_ip_lable = connect_ip_lable;
    }

    public JTextField getConnect_ip_field() {
        return connect_ip_field;
    }

    public void setConnect_ip_field(JTextField connect_ip_field) {
        this.connect_ip_field = connect_ip_field;
    }

    public JLabel getConnect_port_lable() {
        return connect_port_lable;
    }

    public void setConnect_port_lable(JLabel connect_port_lable) {
        this.connect_port_lable = connect_port_lable;
    }

    public JTextField getConnect_port_field() {
        return connect_port_field;
    }

    public void setConnect_port_field(JTextField connect_port_field) {
        this.connect_port_field = connect_port_field;
    }

    public JLabel getConnect_pwd_lable() {
        return connect_pwd_lable;
    }

    public void setConnect_pwd_lable(JLabel connect_pwd_lable) {
        this.connect_pwd_lable = connect_pwd_lable;
    }

    public JTextField getConnect_pwd_field() {
        return connect_pwd_field;
    }

    public void setConnect_pwd_field(JTextField connect_pwd_field) {
        this.connect_pwd_field = connect_pwd_field;
    }

    public JButton getConnect_connect_button() {
        return connect_connect_button;
    }

    public void setConnect_connect_button(JButton connect_connect_button) {
        this.connect_connect_button = connect_connect_button;
    }

    public JLabel getConnected_port_lable() {
        return connected_port_lable;
    }

    public void setConnected_port_lable(JLabel connected_port_lable) {
        this.connected_port_lable = connected_port_lable;
    }

    public JLabel getConnected_pwd_lable() {
        return connected_pwd_lable;
    }

    public void setConnected_pwd_lable(JLabel connected_pwd_lable) {
        this.connected_pwd_lable = connected_pwd_lable;
    }

    public JTextField getConnected_port_field() {
        return connected_port_field;
    }

    public void setConnected_port_field(JTextField connected_port_field) {
        this.connected_port_field = connected_port_field;
    }

    public JTextField getConnected_pwd_field() {
        return connected_pwd_field;
    }

    public void setConnected_pwd_field(JTextField connected_pwd_field) {
        this.connected_pwd_field = connected_pwd_field;
    }

    public void setConnected_start_button(JButton connected_start_button) {
        this.connected_start_button = connected_start_button;
    }

    public JLabel getConnected_savefile_lable() {
        return connected_savefile_lable;
    }

    public void setConnected_savefile_lable(JLabel connected_savefile_lable) {
        this.connected_savefile_lable = connected_savefile_lable;
    }

    public JTextField getConnected_savefile_filechooser() {
        return connected_savefile_filechooser;
    }

    public void setConnected_savefile_filechooser(JTextField connected_savefile_filechooser) {
        this.connected_savefile_filechooser = connected_savefile_filechooser;
    }

    public JTextArea getConsole_log_textarea() {
        return console_log_textarea;
    }

    public void setConsole_log_textarea(JTextArea console_log_textarea) {
        this.console_log_textarea = console_log_textarea;
    }

    public JLabel getSendpage() {
        return sendpage;
    }

    public void setSendpage(JLabel sendpage) {
        this.sendpage = sendpage;
    }

    public JLabel getConnect_successful_filepath_lable() {
        return connect_successful_filepath_lable;
    }

    public void setConnect_successful_filepath_lable(JLabel connect_successful_filepath_lable) {
        this.connect_successful_filepath_lable = connect_successful_filepath_lable;
    }

    public JTextField getConnect_successful_filepath_textfile() {
        return connect_successful_filepath_textfile;
    }

    public void setConnect_successful_filepath_textfile(JTextField connect_successful_filepath_textfile) {
        this.connect_successful_filepath_textfile = connect_successful_filepath_textfile;
    }

    public JButton getSend() {
        return send;
    }

    public void setSend(JButton send) {
        this.send = send;
    }

    public com.client_server.server_thread getServer_thread() {
        return server_thread;
    }

    public void setServer_thread(com.client_server.server_thread server_thread) {
        this.server_thread = server_thread;
    }

    public JButton getConnected_start_button() {
        return connected_start_button;
    }

    public void setConnected_server_checkbox(JCheckBox connected_server_checkbox) {
        this.connected_server_checkbox = connected_server_checkbox;
    }

    public JCheckBox getConnected_server_checkbox() {
        return connected_server_checkbox;
    }


    //构造器
    public ftp()
    {
        //值初始化
       client_thread=null;
       server_thread=null;
        //窗口初始化
        //System.out.println(screenWidth+""+screenHeight);
        this.setBounds(0,0,400,428);
        this.setLocation(screenWidth/2-200,screenHeight/2-200);
        this.setTitle("数据发送器");
        this.setResizable(false);
        this.setLayout(null);
        //添加主panel
        panel=(JPanel)this.getContentPane();
        //添加JPanel
        connect_JPanel=new JPanel();
        connect_JPanel.setLayout(null);
        connect_JPanel.setBounds(0,0,200,200);
        connect_JPanel.setBackground(Color.blue);
        panel.add(connect_JPanel);

        console_JPanel=new JScrollPane();
        console_JPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        console_JPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        console_JPanel.setBounds(0,200,400,200);
        console_JPanel.setBackground(Color.gray);
        panel.add(console_JPanel);

        connected_JPanel=new JPanel();
        connected_JPanel.setLayout(null);
        connected_JPanel.setBounds(200,0,200,200);
        connected_JPanel.setBackground(Color.cyan);
        panel.add(connected_JPanel);

        //JPanel添加控件，赋值
        //connect-JPanel
        connect_ip_lable=new JLabel();
        connect_JPanel.add(connect_ip_lable);
        connect_ip_lable.setBounds(0,0,100,30);
        connect_ip_lable.setText("目的ip");

        connect_ip_field=new JTextField();
        connect_JPanel.add(connect_ip_field);
        connect_ip_field.setBounds(100,0,100,30);

        connect_port_lable=new JLabel();
        connect_JPanel.add(connect_port_lable);
        connect_port_lable.setBounds(0,40,100,30);
        connect_port_lable.setText("目的端口");

        connect_port_field=new JTextField();
        connect_JPanel.add(connect_port_field);
        connect_port_field.setBounds(100,40,100,30);

        connect_pwd_lable=new JLabel();
        connect_JPanel.add(connect_pwd_lable);
        connect_pwd_lable.setBounds(0,80,100,30);
        connect_pwd_lable.setText("连接密码");

        connect_pwd_field=new JTextField();
        connect_JPanel.add(connect_pwd_field);
        connect_pwd_field.setBounds(100,80,100,30);

        connect_connect_button=new JButton();
        connect_JPanel.add(connect_connect_button);
        connect_connect_button.setBounds(50,120,100,50);
        connect_connect_button.setText("连接");
        connect_connect_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                //如果输入为空
                if(connect_ip_field.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null,"目的ip不能为空!","error",0);
                    return;
                }
                if(connect_port_field.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null,"目的端口不能为空!","error",0);
                    return;
                }
                if(connect_pwd_field.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null,"密码不能为空!","error",0);
                    return;
                }
                //服务器连接
                client_thread=new client_thread(connect_ip_field.getText(),Integer.parseInt(connect_port_field.getText()) );

                //send连接密码
                try {
                    System.out.println("发送连接密码");
                    getClient_thread().senddata("@pwd:"+ftp.connect_pwd_field.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        //connected-JPanel
        connected_server_checkbox=new JCheckBox();
        connected_JPanel.add(connected_server_checkbox);
        connected_server_checkbox.setBounds(0,0,100,30);
        connected_server_checkbox.setEnabled(false);
        connected_server_checkbox.setSelected(false);
        connected_server_checkbox.setText("服务状态");

        connected_link_checkbox=new JCheckBox();
        connected_JPanel.add(connected_link_checkbox);
        connected_link_checkbox.setBounds(0,35,100,30);
        connected_link_checkbox.setEnabled(false);
        connected_link_checkbox.setSelected(false);
        connected_link_checkbox.setText("连接状态");

        connected_port_lable=new JLabel();
        connected_JPanel.add(connected_port_lable);
        connected_port_lable.setText("端口");
        connected_port_lable.setBounds(0,70,100,30);

        connected_port_field=new JTextField();
        connected_JPanel.add(connected_port_field);
        connected_port_field.setBounds(100,70,100,30);

        connected_pwd_lable=new JLabel();
        connected_JPanel.add(connected_pwd_lable);
        connected_pwd_lable.setText("密码");
        connected_pwd_lable.setBounds(0,105,100,30);

        connected_pwd_field=new JTextField();
        connected_JPanel.add(connected_pwd_field);
        connected_pwd_field.setBounds(100,105,100,30);

        connected_savefile_lable=new JLabel();
        connected_JPanel.add(connected_savefile_lable);
        connected_savefile_lable.setText("保存路径");
        connected_savefile_lable.setBounds(0,140,100,30);

        connected_savefile_filechooser=new JTextField();
        connected_JPanel.add(connected_savefile_filechooser);
        connected_savefile_filechooser.setBounds(100,140,100,30);

        connected_start_button=new JButton();
        connected_JPanel.add(connected_start_button);
        connected_start_button.setText("创建");
        connected_start_button.setBounds(50,175,100,25);
        connected_start_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                //如果输入为空
                if(connected_port_field.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null,"端口不能为空!","error",0);
                    return;
                }
                if(connected_pwd_field.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null,"密码不能为空!","error",0);
                    return;
                }
                if(connected_savefile_filechooser.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null,"保存文件路径不能为空!","error",0);
                    return;
                }
                if(!new File(connected_savefile_filechooser.getText()).isDirectory() )
                {
                    JOptionPane.showMessageDialog(null,"请填写合法的文件保存路径!","error",0);
                    return;
                }
                try {
                    setServer_thread(new server_thread(Integer.parseInt(connected_port_field.getText())));
                    System.out.println("服务器创建成功");
                    ftp.connected_server_checkbox.setText("消息模式");
                    //服务器创建成功日志
                    ftp.console_log_textarea_append("服务器创建成功");
                    ftp.connect_connect_button.setEnabled(false);
                    ftp.connected_start_button.setEnabled(false);
                } catch (IOException e1)
                {
                    System.out.println("服务器创建失败");
                }

            }
        });

        //console JscrollPanel
        console_log_textarea=new JTextArea();
        console_JPanel.setViewportView(console_log_textarea);
        console_log_textarea.setEnabled(false);
        console_log_textarea_append("日志记录:");



    }
    //connect successful连接成功
    void connect_successful_event(){
        //打印日志

        //改变connect panel
        connect_JPanel.removeAll();

        sendpage=new JLabel("null",JLabel.CENTER);
        sendpage.setBounds(0,0,200,30);
        connect_JPanel.add(sendpage);

        connect_successful_filepath_lable=new JLabel("内容");
        connect_successful_filepath_lable.setBounds(0,40,100,30);
        connect_JPanel.add(connect_successful_filepath_lable);

        connect_successful_filepath_textfile=new JTextField();
        connect_successful_filepath_textfile.setBounds(100,40,100,30);
        connect_JPanel.add(connect_successful_filepath_textfile);

        send=new JButton("发送");
        send.setBounds(50,80,100,30);
        connect_JPanel.add(send);
        send.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("发消息");


                try {
                    if(!ftp.getConnected_server_checkbox().isSelected()) {
                        client_thread.senddata(ftp.connect_successful_filepath_textfile.getText());
                    }
                    else
                        server_thread.senddata(ftp.connect_successful_filepath_textfile.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //内容清空
                ftp.getConnect_successful_filepath_textfile().setText("");
            }
        });
    }
    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    //log控制台信息追加
    void console_log_textarea_append(String str){
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=dateFormat.format(new Date());
        console_log_textarea.append(date+str+"\n");
        //实现自动滚动
        console_log_textarea.setCaretPosition(console_log_textarea.getText().length());
    }
    public static void main(String[] argc)
    {
        getinstance().setVisible(true);
    }
}
