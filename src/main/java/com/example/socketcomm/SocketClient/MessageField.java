package com.example.socketcomm.SocketClient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.socketcomm.SocketClient.friendInfoCard.filePath;


public class MessageField extends ScrollPane
{
    FileWriter fileWriter;
    FileReader fileReader;

    private VBox messageCardBox = new VBox(10);
    public MessageField()
    {
        messageCardBox.setPrefHeight(350);
        messageCardBox.setPrefWidth(640);
        messageCardBox.setAlignment(Pos.TOP_CENTER);
        messageCardBox.setStyle("-fx-background-color: #FFFFFF");

        // 添加高度变化监听器
        messageCardBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.setVvalue(1.0); // 自动滚动到底部
        });

        this.setContent(messageCardBox);
    }

    public void recv(String nickName, String message)
    {
        try {
            fileWriter = new FileWriter(filePath, true);
            fileWriter.write("recv," + nickName + "," + message + "\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RecvMessageCard card = new RecvMessageCard(nickName, message);
        card.setPadding(new Insets(10, 0, 10, 0));
        Platform.runLater(() -> {
            messageCardBox.getChildren().add(card);
            // 自动滚动到底部
            this.setVvalue(1.0);
        });
    }

    public void send(String nickName, String message)
    {
        try {
            fileWriter = new FileWriter(filePath, true);
            fileWriter.write("send," + nickName + "," + message + "\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SendMessageCard card = new SendMessageCard(nickName, message);
        card.setPadding(new Insets(10, 0, 10, 0));
        messageCardBox.getChildren().add(card);
        this.setVvalue(1.0);
    }

    public void clear()
    {
        messageCardBox.getChildren().clear();
    }

    public void initialize()
    {
        try {
            fileReader = new FileReader(filePath);
            // 创建一个文件读取器
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            List<HBox> messageCards = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                String[] str = line.split(",");
                if (str[0].equals("send")) {
                    SendMessageCard card = new SendMessageCard(str[1], str[2]);
                    card.setPadding(new Insets(10, 0, 10, 0));
                    messageCards.add(card);
                } else if (str[0].equals("recv")) {
                    RecvMessageCard card = new RecvMessageCard(str[1], str[2]);
                    card.setPadding(new Insets(10, 0, 10, 0));
                    messageCards.add(card);
                }
            }

// 添加消息卡片到 messageCardBox 以保持原始顺序
            Platform.runLater(() -> {
                messageCardBox.getChildren().addAll(messageCards);
                // 自动滚动到底部
                this.setVvalue(1.0);
            });
//
//            // 逐行读取文件内容
//            while ((line = bufferedReader.readLine()) != null)
//            {
//                String[] str = line.split(",");
//                if (str[0].equals("send"))
//                {
//                    SendMessageCard card = new SendMessageCard(str[1], str[2]);
//                    card.setPadding(new Insets(0, 0, 10, 0));
//                    messageCardBox.getChildren().add(card);
//                    this.setVvalue(1.0);
//                }
//                else if(str[0].equals("recv"))
//                {
//                    RecvMessageCard card = new RecvMessageCard(str[1], str[2]);
//                    card.setPadding(new Insets(0, 0, 10, 0));
//                    Platform.runLater(() -> {
//                        messageCardBox.getChildren().add(card);
//                        // 自动滚动到底部
//                        this.setVvalue(1.0);
//                    });
//                }
//            }

            // 关闭文件读取器
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void offlineRecv(String fPath, String nickName, String message)
    {
        try {
            fileWriter = new FileWriter(fPath, true);
            fileWriter.write("recv," + nickName + "," + message + "\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

class RecvMessageCard extends HBox
{
    public RecvMessageCard(String nickName, String msg)
    {
        Circle userImage = new Circle(24);
        userImage.setFill(Color.rgb(204, 204, 204));

        VBox imageBox = new VBox();
        imageBox.setStyle("-fx-background-color: #FFFFFF");
        imageBox.getChildren().add(userImage);
        imageBox.setPrefWidth(60);
        imageBox.setPrefHeight(60);
        imageBox.setAlignment(Pos.CENTER);

        Label name = new Label(nickName);
        Label message = new Label(msg);

        VBox textBox = new VBox();
        textBox.setStyle("-fx-background-color: #FFFFFF");
        textBox.getChildren().addAll(name, message);
        textBox.setPrefHeight(60);
        textBox.setAlignment(Pos.CENTER_LEFT);

        this.getChildren().addAll(imageBox, textBox);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(60);
        this.setPrefWidth(640);
    }
}

class SendMessageCard extends HBox
{
    public SendMessageCard(String nickName, String msg)
    {
        Circle userImage = new Circle(24);
        userImage.setFill(Color.rgb(204, 204, 204));

        VBox imageBox = new VBox();
        imageBox.setStyle("-fx-background-color: #FFFFFF");
        imageBox.getChildren().add(userImage);
        imageBox.setPrefWidth(60);
        imageBox.setPrefHeight(60);
        imageBox.setAlignment(Pos.CENTER);

        Label name = new Label(nickName);
        Label message = new Label(msg);

        VBox textBox = new VBox();
        textBox.setStyle("-fx-background-color: #FFFFFF");
        textBox.getChildren().addAll(name, message);
        textBox.setPrefHeight(60);
        textBox.setAlignment(Pos.CENTER_RIGHT);

        this.getChildren().addAll(textBox, imageBox);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setPrefHeight(60);
        this.setPrefWidth(640);
    }
}