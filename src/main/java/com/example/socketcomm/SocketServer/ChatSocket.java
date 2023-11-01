package com.example.socketcomm.SocketServer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
//创建一个聊天socket继承自Thread类
public class ChatSocket extends Thread {
    Socket socket;
    private String userID;
    //构造函数
    public ChatSocket(Socket s) {
        this.socket = s;
    }
    //用于得到输出流，写数据
    public void out(String out) {
        try {
            socket.getOutputStream().write((out+"\n").getBytes("UTF-8"));
        } catch (IOException e) {
            ServerManager.getServetManager().remove(this);
            e.printStackTrace();
        };
    }
    @Override
    public void run()
    {
        //当客户端连接到服务器后，服务器给客户端提示
//        out("你已连接到本服务器");
        try {
            //获取socket的输入流
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),"UTF-8"));
            while (true)
            {
                String line = br.readLine();
                String[] socketMsg = line.split(",");
                //连接建立报文格式:"connect, userID"
                //消息报文格式:"msg, recvUserID, <message>"
                if (socketMsg[0].equals("connect"))
                {
                    this.setUserID(socketMsg[1]);
                    System.out.println(this.getUserID() + " has connected to chatServer");
                }
                else if(socketMsg[0].equals("msg"))
                {
                    System.out.println(this.getUserID() + " send to " + socketMsg[1] + ":" + socketMsg[2]);
                    ServerManager.getServetManager().publish(this, socketMsg[1], this.getUserID() + "," + socketMsg[2]);
                }
            }

//            //循环读取数据，当输入流的数据不为空时，把数据写发送到每一个客户端
//            while ((line = br.readLine()) != null)
//            {
//
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getUserID()
    {
        return userID;
    }
}
