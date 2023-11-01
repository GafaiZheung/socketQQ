package com.example.socketcomm.SocketServer;

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
    public void publish(ChatSocket cs, String recvUserID, String out)
    {
        for (int i = 0; i < vector.size(); i++)
        {
            ChatSocket csChatSocket = vector.get(i);
            //把vector中的每一个个体与传进来的线程进行比较，如果不是自己则发送
            
            if (csChatSocket.getUserID().equals(recvUserID))
            {
                csChatSocket.out(out);
            }
//            if(!cs.equals(csChatSocket))
//                csChatSocket.out(out);
        }
    }
}
