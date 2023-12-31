package com.example.socketcomm.LoginServer;

import com.example.socketcomm.Jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

public class LoginServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(8081);
        serverSocket.bind(address);
        System.out.println("Login server has started");
        while (true)
        {
            Jdbc mysql = new Jdbc();
            Socket socket = serverSocket.accept();
            // 启动新线程处理客户端请求
            Thread clientThread = new Thread(() -> {
                try {
                    // 数据库已实现
                    String msg = readData(socket.getInputStream());
                    String[] strmsg = msg.split(",");
                    switch (strmsg[0]) {
                        case "login" -> {
                            OutputStream out = socket.getOutputStream();
                            if (mysql.check_number(strmsg[1], strmsg[2])) out.write(1);
                            else out.write(-1);
                        }
                        case "register" -> {
                            OutputStream out = socket.getOutputStream();
                            int num;
                            do {
                                num = new Random().nextInt(10000) + 10000;
                            } while (!mysql.update_qq(String.valueOf(num), strmsg[1], strmsg[2], strmsg[3]));
                            out.write(String.valueOf(num).getBytes());
                        }
                        case "check" -> {
                            OutputStream out = socket.getOutputStream();
                            //数据库功能
                            if(mysql.checkUid(strmsg[1])) out.write(1);
                            else out.write(-1);
                        }
                        case "verify" -> {
                            OutputStream out = socket.getOutputStream();
                            //数据库功能
                            if(mysql.checkInfo(strmsg[1], strmsg[2])) out.write(1);
                            else out.write(-1);
                        }
                        case "reset" -> {
                            OutputStream out = socket.getOutputStream();
                            //数据库功能
                            if(mysql.updatePassword(strmsg[1], strmsg[2])) out.write(1);
                            else out.write(-1);
                        }
                    }
                    socket.close();
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

