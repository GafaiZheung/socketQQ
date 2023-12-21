package com.example.socketcomm.SocketServer;

import com.example.socketcomm.Jdbc;

import java.util.ArrayList;
import java.util.Vector;

public class ServerManager {
    //单例化处理
    private ServerManager() {}
    private static final ServerManager sm = new ServerManager();
    public static ServerManager getServetManager() {
        return sm;
    }
    //定义一个集合，用来存放不同的客户端
    Vector<ChatSocket> vector = new Vector<ChatSocket>();
    //实现往Vector中添加个体
    public void add(ChatSocket cs) {
        vector.add(cs);
    }
    //实现删除vector中断开连接的线程
    public void remove(ChatSocket cs) {
        vector.remove(cs);
    }
    //把获取的消息发布给除自己以外的其他客户端
    public void publish(ChatSocket cs, String recvUserID, String sendUserID, String message)
    {
        String out = sendUserID + "," + message;
        boolean isSendSuccess = false;
        Jdbc mysql = new Jdbc();

        boolean isAdded = false;
        for(String friendID: mysql.select_friend(sendUserID))
        {
            if (recvUserID.equals(friendID)) {
                isAdded = true;
                break;
            }
        }
        if(!isAdded)
        {
            mysql.Initialize(sendUserID, recvUserID);
        }

        for (ChatSocket csChatSocket : vector) {
            //把vector中的每一个个体与传进来的线程进行比较，如果不是自己则发送
            if(csChatSocket.getUserID() != null)
            {
                if (csChatSocket.getUserID().equals(recvUserID)) {
                    if (!sendUserID.equals(recvUserID)) {
                        mysql.set_message("send", sendUserID, recvUserID, message);
                        mysql.set_message("recv", recvUserID, sendUserID, message);
                    }
                    csChatSocket.out(out);
                    isSendSuccess = true;
                }
            }

//            if(!cs.equals(csChatSocket))
//                csChatSocket.out(out);
        }
        if(!isSendSuccess)
        {
            mysql.set_message("sendFail", sendUserID, recvUserID, message);
        }
    }
}
