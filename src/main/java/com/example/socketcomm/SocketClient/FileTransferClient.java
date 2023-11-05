package com.example.socketcomm.SocketClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class FileTransferClient {
    private String serverIp;
    private int serverPort;
    private Socket clientSendProgress;
    private Socket clientRecv;
    private DataOutputStream dos;
    private DataInputStream dis;
    private FileInputStream fis;
    private FileOutputStream fos;
    private boolean stopReceiving = false;
    private static DecimalFormat df = null;

    private FileTransferClient() {}
    private static final FileTransferClient ftc = new FileTransferClient();
    public static FileTransferClient getFtc()
    {
        return ftc;
    }
    public void connect(String serverIp, int serverPort)
    {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        try
        {
            clientRecv = new Socket(this.serverIp, 8989);
            recvFile(clientRecv);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    private void recvFile(Socket client)
    {
        new Thread(){
            @Override
            public void run()
            {
                try {
                    dos = new DataOutputStream(client.getOutputStream());
                    dis = new DataInputStream(client.getInputStream());
                    System.out.println("Client[port:" + client.getLocalPort() + "] Connected to the server");

                    // 文件名和长度
                    int fileNameLength = dis.readInt();
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dis.readFully(fileNameBytes);
                    String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
                    long fileLength = dis.readLong();

                    int recvIDLength = dis.readInt();
                    byte[] recvIDBytes = new byte[recvIDLength];
                    dis.readFully(recvIDBytes);
                    String recvID = new String(recvIDBytes, StandardCharsets.UTF_8);

                    System.out.println(recvID);

                    int userIDLength = dis.readInt();
                    byte[] userIDBytes = new byte[userIDLength];
                    dis.readFully(userIDBytes);
                    String sendID = new String(userIDBytes, StandardCharsets.UTF_8);

                    System.out.println(sendID);

                    File directory = new File("./clientRecv");
                    if (!directory.exists())
                    {
                        if (directory.mkdir())
                        {
                            System.out.println("create success");
                        }
                        else System.out.println("fail");
                    }

                    File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                    fos = new FileOutputStream(file);

                    byte[] bytes = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = dis.read(bytes, 0, bytes.length)) != -1) {
                        fos.write(bytes, 0, bytesRead);
                        fos.flush();
                    }
                    System.out.println("======== 文件接收成功 [File Name：" + fileName + "]========");
                    client.close();

//                    if(!recvID.equals(ChatWindow.currentUserID))
//                        file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    connect(serverIp, serverPort);
                }
            }
        }.start();
    }

    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }

    public void sendFile(String filePath) {
        try {
            clientSendProgress = new Socket(this.serverIp, 8900);
            File file = new File(filePath);
            if (file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(clientSendProgress.getOutputStream());

                // 发送文件名和长度
                byte[] fileNameBytes = file.getName().getBytes(StandardCharsets.UTF_8);
                dos.writeInt(fileNameBytes.length);
                dos.write(fileNameBytes);
                dos.writeLong(file.length());

                dos.writeInt(ChatWindow.currentUserID.length());
                dos.write(ChatWindow.currentUserID.getBytes(StandardCharsets.UTF_8));

                dos.writeInt(ChatWindow.chatWithID.length());
                dos.write(ChatWindow.chatWithID.getBytes(StandardCharsets.UTF_8));

                // 开始传输文件
                System.out.println("======== 开始传输文件 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    ChatWindow.chatMessageField.progressBar((double) (progress) / file.length());
                }
                System.out.println();
                System.out.println("======== 文件传输成功 ========");

                fis.close();
                dos.close();
                clientSendProgress.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
