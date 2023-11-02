package com.example.socketcomm.SocketServer;


import com.example.socketcomm.Jdbc;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
                InputStream in = socket.getInputStream();
                byte[] identifierBytes = new byte[5];
                int bytesRead = in.read(identifierBytes);
                String identifier = new String(identifierBytes, 0, bytesRead);

                switch (identifier) {
                    case "file:" -> {
                        try (FileOutputStream fileOutputStream = new FileOutputStream("received_file.txt")) {
                            byte[] buffer = new byte[1024];
                            while ((bytesRead = in.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                        }
                        System.out.println("Received a file.");
                    }
                    case "cnct:" -> {
                        String line = br.readLine();
                        Jdbc mysql = new Jdbc();
//                    System.out.println(line);
                        String[] socketMsg = line.split(",");

                        this.setUserID(socketMsg[0]);
                        System.out.println(this.getUserID() + " has connected to chatServer");

                        //上线获取所有的sendFail讯息
                        ArrayList<String> sendID = mysql.get_friend_userID(this.getUserID());
                        for (String value : sendID) {
                            if (!this.getUserID().equals(value)) {
                                ArrayList<String> message = mysql.get_send_fail_message(value, this.getUserID());
                                for (String s : message) {
                                    ServerManager.getServetManager().publish(this, this.getUserID(), value, s);
                                }
                            }
                        }
                    }
                    case "mesg:" -> {
                        String line = br.readLine();
                        Jdbc mysql = new Jdbc();
                        String[] socketMsg = line.split(",");
//                    System.out.println(line);
                        System.out.println(this.getUserID() + " send to " + socketMsg[0] + ":" + socketMsg[1]);
                        mysql.Initialize(this.getUserID(), socketMsg[1]);
                        mysql.Initialize(socketMsg[1], this.getUserID());
                        ServerManager.getServetManager().publish(this, socketMsg[0], this.getUserID(), socketMsg[1]);
                    }
                }


                //连接建立报文格式:"connect, userID"
                //消息报文格式:"msg, recvUserID, <message>"
//                if (socketMsg[0].equals("connect"))
//                {
//
//                }
//                else if(socketMsg[0].equals("msg"))
//                {
//
//                }
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
