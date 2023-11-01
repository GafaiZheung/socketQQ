package com.example.socketcomm.SocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatManager {
    private ChatManager() {}
    private static final ChatManager instance = new ChatManager();
    public static ChatManager getCM() {
        return instance;
    }

    MainWindow window;//定义窗体变量
    Socket socket;
    String IP;//定义IP变量，用来存放传进来的IP地址
    BufferedReader reader;//定义读取数据的输入流
    PrintWriter writer;//定义写数据的输出流
    //设置给窗体中添加文字的方法，实现消息的显示
    public void setWindow(MainWindow window){
        this.window = window;
        window.appendText("文本框已经和ChatManager绑定了");
    }
    public void connect(String ip) {
        //把传进来的ip赋值给IP
        this.IP = ip;
        //定义一个线程的执行
        new Thread() {
            public void run() {
                try {
                    //用于接收ip地址，传到socket中
                    socket = new  Socket(IP, 12345);
                    writer = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream(),"UTF-8"));
                    reader = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream(), "UTF-8"));

                    String line;
                    //点读取到的数据不为空时，把读取的数据输出到窗口文本区中
                    while ((line = reader.readLine()) != null) {
                        window.appendText("收到: "+line);
                    }
                    writer.close();//关闭输入输出流
                    reader.close();
                    writer = null;
                    reader = null;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            };
        }.start();

    }
    //这是聊天输入框的后台处理，当按了发送后，会把输入框中的数据发送出去
    public void send(String out) {
        if(writer != null) {
            writer.write(out+"\n");
            writer.flush();//写完数据后必须刷新缓存区，才能发出去
        }else {
            window.appendText("当前连接已断开");
        }
    }
}

