package com.example.socketcomm.SocketServer;

import java.io.File;
import java.util.Vector;

public class SendServerManager
{
    private static final SendServerManager ssm = new SendServerManager();

    private Vector<SendServer> vector = new Vector<SendServer>();
    private SendServerManager(){}

    public static SendServerManager getSsm()
    {
        return ssm;
    }

    public void add(SendServer ss)
    {
        vector.add(ss);
    }

    public void remove(SendServer ss)
    {
        vector.remove(ss);
    }

    public void publish(String sendUserID, String recvUserID, File file)
    {
        System.out.println(vector.size());
        while (vector.size() > 0)
        {
            SendServer sendServer = vector.get(0);
            sendServer.sendProcess(sendUserID, recvUserID, file);
            remove(sendServer);
        }
    }
}
