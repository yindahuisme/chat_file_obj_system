package com.data;

import java.io.Serializable;

public class woss_data implements Serializable {
    //用户名
    private String username;
    //用户访问服务器协议地址
    private String protocol_address;
    //上线时间
    private String online;
    //线上时长s
    private int online_time=0;
    //上线时间戳
    private  String online_time_stamp;
    //用户ip
    private String ip;

    private static final long serialVersionUID = 7321735340980031162L;
    //构造函数
    public woss_data(String username,String protocol_address,String online,String ip,String online_time_stamp)
    {

        //属性一一对应
setUsername(username);
setProtocol_address(protocol_address);
setOnline(online);
setIp(ip);
setOnline_time_stamp(online_time_stamp);
    }

    //访问器


    public String getOnline_time_stamp() {
        return online_time_stamp;
    }

    public void setOnline_time_stamp(String online_time_stamp) {
        this.online_time_stamp = online_time_stamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProtocol_address() {
        return protocol_address;
    }

    public void setProtocol_address(String protocol_address) {
        this.protocol_address = protocol_address;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public int getOnline_time() {
        return online_time;
    }

    public void setOnline_time(int online_time) {
        this.online_time = online_time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
