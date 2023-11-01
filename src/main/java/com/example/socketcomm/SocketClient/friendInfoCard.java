package com.example.socketcomm.SocketClient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class friendInfoCard extends HBox {
    protected static String filePath;

    String nickName;
    String userID;
    Label userIDText;

    private boolean isClicked = false; // 新增一个标志来跟踪是否已经点击
    protected static ArrayList<friendInfoCard> allFriendInfoCards = new ArrayList<>();


    public friendInfoCard(String name, String ID) {
        nickName = name;
        userID = ID;

        Label nickNameText = new Label(nickName);
        userIDText = new Label(userID);

        Circle userImage = new Circle(24);
        userImage.setFill(Color.rgb(204, 204, 204));

        VBox textBox = new VBox(0);
        textBox.getChildren().addAll(nickNameText, userIDText);
        textBox.setAlignment(Pos.CENTER_LEFT);

        getChildren().add(userImage);
        getChildren().add(textBox);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);
        setPadding(new Insets(0, 0, 0, 5));

        // 设置HBox的首选大小
        setPrefWidth(265);
        setPrefHeight(80);
        setStyle("-fx-background-color: #FFFFFF");

        allFriendInfoCards.add(this); // 将新创建的对象添加到集合中

        this.setOnMouseClicked(this::clickFriendInfoCard);
    }

    public void setMessageStatus(boolean hasMessage)
    {
        if(hasMessage)
        {
            Platform.runLater(() -> {
                userIDText.setText("有新消息");
                userIDText.setStyle("-fx-background-color: #CCCCCC");
            });
        }
        else
        {
            userIDText.setText(userID);
            userIDText.setStyle("-fx-background-color: transparent");
        }
    }


    void setNickName(String name) {
        nickName = name;
    }

    void setUserID(String ID) {
        userID = ID;
    }

    String getNickName() {
        return nickName;
    }

    String getUserID() {
        return userID;
    }

    void clickFriendInfoCard(MouseEvent event)
    {
        if (isClicked) {
            // 恢复背景颜色
            this.setStyle("-fx-background-color: #FFFFFF");
        } else {
            // 设置选中状态的背景颜色
            this.setStyle("-fx-background-color: #CCCCCC");
            ChatWindow.chatWithID = userID;

            this.setMessageStatus(false);

            String sendID = ChatWindow.currentUserID;
            String recvID = ChatWindow.chatWithID;
            filePath = sendID + recvID + ".txt";
            try {
                new FileWriter(filePath, true).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ChatWindow.chatMessageField.clear();
//            System.out.println(filePath);
            ChatWindow.chatMessageField.initialize();
//            ChatWindow.chatTextArea.setText("");
        }

        isClicked = !isClicked; // 切换点击状态

        // 循环处理其他对象
        for (friendInfoCard card : allFriendInfoCards) {
            if (card != this) {
                card.setStyle("-fx-background-color: #FFFFFF");
                card.isClicked = false;
            }
        }

        ChatWindow.chatTitle.setNickName(nickName);
    }
}
