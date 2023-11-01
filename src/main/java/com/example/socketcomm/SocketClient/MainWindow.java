package com.example.socketcomm.SocketClient;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame {

    private JPanel contentPane;
    private JTextField ip;
    private JTextField send;
    private JTextArea txt;

    //这是主窗体的定义
    public MainWindow() {
        setTitle("聊天室");//窗体的标题
        setAlwaysOnTop(true);//设置窗体的属性，总是在最上面显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 638, 460);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        //这是放置聊天内容的地方。文本区组件
        ip = new JTextField();
        ip.setText("127.0.0.1");
        ip.setColumns(10);

        send = new JTextField();
        //键盘事件，实现当输完要发送的内容后，直接按回车键，实现发送
        send.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ChatManager.getCM().send(send.getText());
                    appendText("我说：" + send.getText());
                    send.setText("");
                }
            }
        });
        send.setText("");
        send.setColumns(10);
        //发送按钮的定义，功能
        JButton button = new JButton("发送");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                //鼠标单击发送按钮后，调用聊天管理的send方法，把输入框中的数据发出去
                ChatManager.getCM().send(send.getText());
                //向聊天窗体区域添加自己发送的消息
                appendText("我说：" + send.getText());
                //发送玩后，是输入框中内容为空
                send.setText("");
            }
        });
        //定义一个连接到服务器的按钮
        JButton button_1 = new JButton("连接到服务器");
        button_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                //单击按钮后，把ip地址传递到socket进程中，实现连接
                ChatManager.getCM().connect(ip.getText());
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(ip, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(button_1, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(send, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(button, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)))
                                .addGap(0))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(ip, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button_1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
                                .addGap(2)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(send, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)))
        );

        txt = new JTextArea();
        scrollPane.setViewportView(txt);
        txt.setText("Ready...");
        contentPane.setLayout(gl_contentPane);

    }

    //定义一个追加文本的方法
    public void appendText(String in) {
        txt.append("\n" + in);
    }
}