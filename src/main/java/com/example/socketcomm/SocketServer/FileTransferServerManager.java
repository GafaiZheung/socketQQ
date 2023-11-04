package com.example.socketcomm.SocketServer;

import java.util.Vector;

public class FileTransferServerManager
{
    private FileTransferServerManager(){}

    private static final FileTransferServerManager ftsm = new FileTransferServerManager();
    private Vector<FileTransferServer.SendServer> vector = new Vector<FileTransferServer.SendServer>();

    public static FileTransferServerManager getFtsm() {
        return ftsm;
    }

    public void add(FileTransferServer.SendServer server){vector.add(server);}

    public void remove(FileTransferServer.Task task){vector.remove(task);}

    public void publish(String recvUserID, String sendUserID, String sendFilePath)
    {
        System.out.println(vector.size());
//        for (FileTransferServer.SendServer value : vector)
//        {
//            System.out.println(value.getUserID());
//
////            if (value.getUserID().equals(recvUserID))
////            {
////                System.out.println(222);
//////                value.sendFile(sendFilePath);
////            }
//        }
    }

}
