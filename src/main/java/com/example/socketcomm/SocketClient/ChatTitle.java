package com.example.socketcomm.SocketClient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class ChatTitle extends VBox
{
    String nickName;
    private Label nameText;
    public ChatTitle(String name)
    {
        nickName = name;

        nameText = new Label(nickName);
        nameText.setStyle("-fx-font-size: 20");

        this.setPadding(new Insets(0,0,0,30));
        this.getChildren().add(nameText);
        this.setPrefHeight(60);
        this.setPrefWidth(666);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: #FFFFFF");
    }

    String getNickName(){return nickName;}
    void setNickName(String name)
    {
        nickName = name;
        nameText.setText(name);
    }

}
