package com.example.socketcomm.SocketServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileServerSocket
{
    public static void main(String[] args)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try (ServerSocket recvServer = new ServerSocket(8900))
                {
                    while (true)
                    {
                        Socket recvSocket = recvServer.accept();

                        RecvServer recv = new RecvServer(recvSocket);
                        new Thread(recv).start();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        new Thread()
        {
            @Override
            public void run()
            {
                try (ServerSocket sendServer = new ServerSocket(8989))
                {
                    while (true)
                    {
                        Socket sendSocket = sendServer.accept();

                        SendServer send = new SendServer(sendSocket);
                        new Thread(send).start();
                        SendServerManager.getSsm().add(send);
                    }
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

            }
        }.start();
    }
}
