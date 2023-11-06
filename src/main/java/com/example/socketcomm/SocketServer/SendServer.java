package com.example.socketcomm.SocketServer;

import com.example.socketcomm.SocketClient.ChatWindow;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SendServer implements Runnable
{
    private String filePath;
    private Socket socket;
    private String sendUserID;
    private String recvUserID;
    private File file;

    public SendServer(Socket socket)
    {
        this.socket = socket;
    }
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public void sendProcess(String sendUserID, String recvUserID, File file)
    {
        this.sendUserID = sendUserID;
        this.recvUserID = recvUserID;
        this.file = file;

        try {
            if (file != null)
            {
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    // 发送文件名和长度
                    byte[] fileNameBytes = file.getName().getBytes(StandardCharsets.UTF_8);
                    dos.writeInt(fileNameBytes.length);
                    dos.write(fileNameBytes);
                    dos.writeLong(file.length());

                    dos.writeInt(recvUserID.length());
                    dos.write(recvUserID.getBytes(StandardCharsets.UTF_8));

                    dos.writeInt(sendUserID.length());
                    dos.write(sendUserID.getBytes(StandardCharsets.UTF_8));

                    // 开始传输文件
                    System.out.println("======== 开始传输文件 ========");
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    long progress = 0;
                    while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                        dos.write(bytes, 0, length);
                        dos.flush();
                        progress += length;
                    }
                    System.out.println();
                    System.out.println("======== 文件传输成功 ========");
                    socket.close();
                }
            }
        } catch (Exception e) {
            SendServerManager.getSsm().remove(this);
        }
    }
    @Override
    public void run()
    {

    }
}



