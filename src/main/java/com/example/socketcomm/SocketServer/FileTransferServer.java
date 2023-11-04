package com.example.socketcomm.SocketServer;


import com.example.socketcomm.SocketClient.ChatWindow;

import java.io.*;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;


public class FileTransferServer extends ServerSocket {

    private static final int SERVER_PORT = 8900; // 服务端端口

    private static DecimalFormat df = null;

    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    public FileTransferServer() throws Exception {
        super(SERVER_PORT);
    }

    /**
     * 使用线程处理每个客户端传输的文件
     * @throws Exception
     */
    public void load() throws Exception {
//            ServerSocket sendServer = new ServerSocket(8989);
        while (true) {
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = this.accept();
//            Socket sendSocket = sendServer.accept();
            /**
             * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
             * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
             * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
             */
            // 每接收到一个Socket就建立一个新的线程来处理它
            Task task = new Task(socket);
//            SendServer server = new SendServer(sendSocket);
//            new Thread(server).start();
            new Thread(task).start();
//            FileTransferServerManager.getFtsm().add(server);
        }
    }

    /**
     * 处理客户端传输过来的文件线程类
     */
    public class Task implements Runnable {

        private Socket socket;

        private DataInputStream dis;

        private FileOutputStream fos;

        private String userID;

        public Task(Socket socket) {
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
                System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");

                FileTransferServerManager.getFtsm().publish(recvID, this.userID, "./directory/" + fileName);
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

    public class SendServer implements Runnable
    {
        private String filePath;
        private Socket socket;
        private File file;

        public SendServer(Socket socket)
        {
            this.socket = socket;
        }
        public void setFilePath(String filePath)
        {
            this.filePath = filePath;
        }
        @Override
        public void run()
        {
            try {
                if(filePath != null)
                    file = new File(filePath);
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    // 发送文件名和长度
                    byte[] fileNameBytes = file.getName().getBytes(StandardCharsets.UTF_8);
                    dos.writeInt(fileNameBytes.length);
                    dos.write(fileNameBytes);
                    dos.writeLong(file.length());

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
                    dos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化文件大小
     * @param length
     * @return
     */
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


    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        try {
            FileTransferServer server = new FileTransferServer(); // 启动服务端
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
