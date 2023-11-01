package com.example.socketcomm.SocketClient;

import java.awt.EventQueue;
public class StartClient {

    public static void main(String[] args) {
        //将MainWiondow中的代码复制到这，实现通过主方法启动程序
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow frame = new MainWindow();
                    frame.setVisible(true);
                    ChatManager.getCM().setWindow(frame);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

