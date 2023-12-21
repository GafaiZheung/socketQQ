package com.example.socketcomm.FriendListServer;

import com.example.socketcomm.Jdbc;
import com.example.socketcomm.SocketServer.ServerManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class FriendListServer
{
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(10001);
        serverSocket.bind(address);
        System.out.println("friend list server has started");
        Jdbc mysql = new Jdbc();

        while (true) {
            Socket socket = serverSocket.accept();
            // 启动新线程处理客户端请求
            Thread clientThread = new Thread(() -> {
                try {

                    String in = readData(socket.getInputStream());
                    String[] str = in.split(",");

                    switch (str[0]) {
                        case "pull" -> {
                            System.out.println(str[1]);

                            //读取数据库中的userID对应的friendlist，发回客户端
                            //已实现
                            OutputStream out = socket.getOutputStream();
                            ArrayList<String> s = mysql.select_friend(str[1]);
                            for (String value : s) {
                                out.write(value.getBytes());
                                out.flush();
                            }
                            out.write(("end" + "\n").getBytes());
                            out.flush();
                            socket.close();
                        }
                        case "delete" -> mysql.delete_friend(str[1], str[2]);
                        case "search" -> {
                            System.out.println(str[2]);
                            OutputStream out = socket.getOutputStream();
                            ArrayList<String> returnMessage = mysql.addFriend(str[2]);
                            for(String value: returnMessage)
                            {
                                System.out.println(value);
                                out.write(value.getBytes());
                                out.flush();
                            }
                            out.write(("end" + "\n").getBytes());
                            out.flush();
                        }
                        case "add" ->
                        {
                            mysql.add_friend(str[1], str[2]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientThread.start();
        }
    }

    public static String readData(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return br.readLine();  // 从客户端读取一行数据
    }
}
