package com.example.socketcomm.SocketClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ContactorCard extends HBox
{
    String nickName;
    String userID;
    Label userIDText;

    VBox rightBox;

    private boolean isClicked = false; // 新增一个标志来跟踪是否已经点击
    protected static ArrayList<ContactorCard> allContactorCards = new ArrayList<>();


    public ContactorCard(String name, String ID, VBox rightBox) {
        nickName = name;
        userID = ID;
        this.rightBox = rightBox;

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

        allContactorCards.add(this); // 将新创建的对象添加到集合中

        this.setOnMouseClicked(this::clickFriendInfoCard);
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

    protected boolean getIsClicked() {
        return isClicked;
    }

    protected void setIsClicked(boolean clicked) {
        isClicked = clicked;
    }


    void clickFriendInfoCard(MouseEvent event)
    {
        if (isClicked) {
            // 恢复背景颜色
            this.setStyle("-fx-background-color: #FFFFFF");
        } else {
            // 设置选中状态的背景颜色
            this.setStyle("-fx-background-color: #CCCCCC");


            if (!this.userID.equals(ChatWindow.currentUserID))
            {
                Button deleteFriendButton = new Button("删除" + this.userID);
                deleteFriendButton.setStyle("-fx-background-color: #CCCCCC");
//            deleteFriendButton.setPadding(new Insets(100, 0, 0, 0));
                deleteFriendButton.setPrefWidth(200);
                deleteFriendButton.setPrefHeight(50);

                deleteFriendButton.setOnAction(ActionEvent -> {
                    ChatWindow.deleteFriendAction(this.userID);
                    rightBox.getChildren().clear();
                });

                HBox deleteBox = new HBox();
                deleteBox.getChildren().add(deleteFriendButton);
                deleteBox.setPrefWidth(666);
                deleteBox.setPrefHeight(300);
                deleteBox.setAlignment(Pos.CENTER);
                ChatWindow.contactTitle.setNickName("好友管理");
                rightBox.getChildren().clear();
                rightBox.getChildren().addAll(ChatWindow.contactTitle, deleteBox);
            }
            else
            {
                rightBox.getChildren().clear();
                rightBox.getChildren().add(ChatWindow.contactTitle);
            }
        }

        isClicked = !isClicked; // 切换点击状态

        // 循环处理其他对象
        for (ContactorCard card : allContactorCards) {
            if (card != this) {
                card.setStyle("-fx-background-color: #FFFFFF");
                card.isClicked = false;
            }
        }
    }
}
