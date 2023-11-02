package com.example.socketcomm.SocketClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class chatClient {
    private chatClient() {}
    private static final chatClient instance = new chatClient();
    public static chatClient getCM() {
        return instance;
    }

    Socket socket;
    String IP;//定义IP变量，用来存放传进来的IP地址
    BufferedReader reader;//定义读取数据的输入流
    PrintWriter writer;//定义写数据的输出流

    OutputStream fileWriter;
    //设置给窗体中添加文字的方法，实现消息的显示
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

                    fileWriter = socket.getOutputStream();

                    String line;
                    //点读取到的数据不为空时，把读取的数据输出到窗口文本区中
                    //数据格式 senderID,msg
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        String[] str = line.split(",");

                        if(str[0].equals(ChatWindow.chatWithID))
                            ChatWindow.chatMessageField.recv(ChatWindow.chatTitle.getNickName(), str[1]);
                        else
                        {
                            for (int i = 0; i < friendInfoCard.allFriendInfoCards.size(); i++)
                                if(str[0].equals(friendInfoCard.allFriendInfoCards.get(i).getUserID()))
                                {
                                    friendInfoCard.allFriendInfoCards.get(i).setMessageStatus(true);
                                    String fPath = ChatWindow.currentUserID + str[0] + ".txt";
                                    new MessageField().offlineRecv(fPath, friendInfoCard.allFriendInfoCards.get(i).getNickName(), str[1]);
                                }
                        }
//                        ChatWindow.chatTextArea.appendText("收到: " + line + "\n");
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

            }
        }.start();

    }
    //这是聊天输入框的后台处理，当按了发送后，会把输入框中的数据发送出去
    public void send(String identifier, String out)
    {
        if(writer != null) {
            writer.write(identifier);
            writer.flush();//写完数据后必须刷新缓存区，才能发出去
            writer.write(out + "\n");
            writer.flush();
        }else {
            ChatWindow.chatMessageField.recv("system", "connection refused");
//            ChatWindow.chatTextArea.appendText("当前连接已断开");
        }
    }

    public void sendFile(String filePath)
    {
        String fileIdentifier = "file:";
        try {
            fileWriter.write(fileIdentifier.getBytes());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        File fileToSend = new File(filePath);

        try (FileInputStream fileInputStream = new FileInputStream(fileToSend))
        {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1)
            {
                fileWriter.write(buffer, 0, bytesRead);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}

