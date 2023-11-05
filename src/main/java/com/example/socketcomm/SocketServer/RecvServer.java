package com.example.socketcomm.SocketServer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RecvServer implements Runnable {

    private Socket socket;

    private DataInputStream dis;

    private FileOutputStream fos;

    private String userID;

    public RecvServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());

            // 文件名和长度
            int fileNameLength = dis.readInt();
            byte[] fileNameBytes = new byte[fileNameLength];
            dis.readFully(fileNameBytes);
            String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
            long fileLength = dis.readLong();

            int userIDLength = dis.readInt();
            byte[] userIDBytes = new byte[userIDLength];
            dis.readFully(userIDBytes);
            setUserID(new String(userIDBytes, StandardCharsets.UTF_8));

            System.out.println(this.userID);

            int recvIDLength = dis.readInt();
            byte[] recvIDBytes = new byte[recvIDLength];
            dis.readFully(recvIDBytes);
            String recvID = new String(recvIDBytes, StandardCharsets.UTF_8);

            System.out.println(recvID);

            File directory = new File("./recvFile");
            if (!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
            fos = new FileOutputStream(file);

            // 开始接收文件内容
            byte[] bytes = new byte[1024];
            int bytesRead;

            while ((bytesRead = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, bytesRead);
                fos.flush();
            }
            System.out.println("======== 文件接收成功 [File Name：" + fileName + "]========");

            SendServerManager.getSsm().publish(userID, recvID, file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (dis != null)
                    dis.close();
                socket.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setUserID(String uid)
    {
        this.userID = uid;
    }

    public String getUserID()
    {
        return this.userID;
    }
}

