package com.example.socketcomm.SocketClient;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class SelectCard extends HBox {
    private String nickName;
    private String userID;
    private Label userIDText;
    private boolean isClicked = false;
    private static ArrayList<SelectCard> selectedCards = new ArrayList<>();

    public SelectCard(String name, String ID) {
        this.nickName = name;
        this.userID = ID;

        Label nickNameText = new Label(nickName);
        this.userIDText = new Label(userID);

        Circle userImage = new Circle(24);
        userImage.setFill(Color.rgb(204, 204, 204));

        VBox textBox = new VBox(0);
        textBox.getChildren().addAll(nickNameText, userIDText);
        textBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        this.getChildren().add(userImage);
        this.getChildren().add(textBox);
        this.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setPadding(new javafx.geometry.Insets(0, 0, 0, 5));

        // 设置HBox的首选大小
        this.setPrefWidth(265);
        this.setPrefHeight(80);
        this.setStyle("-fx-background-color: #FFFFFF");

        this.setOnMouseClicked(this::clickFriendInfoCard);
    }

    public void setMessageStatus(boolean hasMessage) {
        if (hasMessage) {
            javafx.application.Platform.runLater(() -> {
                this.userIDText.setText("有新消息");
                this.userIDText.setStyle("-fx-background-color: #CCCCCC");
            });
        } else {
            this.userIDText.setText(userID);
            this.userIDText.setStyle("-fx-background-color: transparent");
        }
    }

    void setNickName(String name) {
        this.nickName = name;
    }

    void setUserID(String ID) {
        this.userID = ID;
    }

    String getNickName() {
        return nickName;
    }

    String getUserID() {
        return userID;
    }

    void clickFriendInfoCard(MouseEvent event) {
        if (isClicked) {
            // 已经选中，取消选中状态
            this.setStyle("-fx-background-color: #FFFFFF");
            selectedCards.remove(this);
        } else {
            // 未选中，添加到选中的卡片列表
            this.setStyle("-fx-background-color: #CCCCCC");
            selectedCards.add(this);
        }

        isClicked = !isClicked;
    }

    public static ArrayList<SelectCard> getSelectedCards() {
        return selectedCards;
    }
}
