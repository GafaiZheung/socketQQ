package com.example.socketcomm.SocketClient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

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
    CustomProgressBar bar;

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

    public void sendFile(String nickName, String fileName, String fileSize)
    {
        try {
            fileWriter = new FileWriter(filePath, true);
            fileWriter.write("sendFile," + nickName + "," + fileName + "," + fileSize + "\n");
            fileWriter.close();
        }catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        SendFileCard card = new SendFileCard(nickName, fileName, fileSize);
        card.setPadding(new Insets(10, 0, 10, 0));
        messageCardBox.getChildren().add(card);

        bar = new CustomProgressBar();
        bar.setPadding(new Insets(10, 0, 10, 0));
        messageCardBox.getChildren().add(bar);
        this.setVvalue(1.0);
    }

    public void progressBar(double progress)
    {
//        if (progress == 1.0)
//            messageCardBox.getChildren().remove(bar);
//        else
            bar.setProgress(progress);

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
                switch (str[0]) {
                    case "send" -> {
                        SendMessageCard card = new SendMessageCard(str[1], str[2]);
                        card.setPadding(new Insets(10, 0, 10, 0));
                        messageCards.add(card);
                    }
                    case "recv" -> {
                        RecvMessageCard card = new RecvMessageCard(str[1], str[2]);
                        card.setPadding(new Insets(10, 0, 10, 0));
                        messageCards.add(card);
                    }
                    case "sendFile" -> {
                        SendFileCard card = new SendFileCard(str[1], str[2], str[3]);
                        card.setPadding(new Insets(10, 0, 10, 0));
                        messageCards.add(card);
                    }
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

class SendFileCard extends HBox
{
    public SendFileCard(String nickName, String fileName, String fileSize)
    {
        Circle userImage = new Circle(24);
        userImage.setFill(Color.rgb(204, 204, 204));

        VBox imageBox = new VBox();
        imageBox.setStyle("-fx-background-color: #FFFFFF");
        imageBox.getChildren().add(userImage);
        imageBox.setPrefWidth(60);
        imageBox.setPrefHeight(120);
        imageBox.setAlignment(Pos.TOP_CENTER);
        imageBox.setPadding(new Insets(6));

        Label name = new Label(nickName);
        Label file = new Label(fileName);
        Label size = new Label(fileSize);

        VBox fileInfoBox = new VBox();
        fileInfoBox.getChildren().addAll(file, size);
        fileInfoBox.setMinHeight(60);
        fileInfoBox.setPrefWidth(180);
        fileInfoBox.setAlignment(Pos.CENTER_LEFT);
        fileInfoBox.setPadding(new Insets(0, 0, 0, 20));

        Rectangle fileIcon = new Rectangle(40, 40);
        fileIcon.setFill(Color.rgb(255, 255, 255));
        VBox fileIconBox = new VBox();
        fileIconBox.getChildren().addAll(fileIcon);
        fileIconBox.setAlignment(Pos.CENTER);
        fileIconBox.setPrefWidth(60);
        fileIconBox.setMinHeight(60);

        HBox fileBox = new HBox();
        fileBox.getChildren().addAll(fileInfoBox, fileIconBox);
        fileBox.setStyle("-fx-background-color: #CCCCCC");

        VBox leftSide = new VBox();
        leftSide.getChildren().addAll(name,fileBox);
        leftSide.setAlignment(Pos.CENTER_RIGHT);
        leftSide.setPrefHeight(120);

        this.getChildren().addAll(leftSide, imageBox);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setPrefHeight(120);
        this.setPrefWidth(640);
    }
}

class CustomProgressBar extends HBox {
    private double progress;
    private Label progressLabel;

    public CustomProgressBar() {
        progress = 0;
        progressLabel = new Label("0%"); // 初始显示为0%
        initializeUI();
    }

    private void initializeUI() {
        int numberOfRectangles = 400; // 进度条由多少个矩形组成
        double rectangleWidth = 1; // 每个矩形的宽度
        double totalWidth = numberOfRectangles * rectangleWidth;

        for (int i = 0; i < numberOfRectangles; i++) {
            Rectangle rectangle = new Rectangle(rectangleWidth, 2); // 矩形的高度
            rectangle.setFill(Color.BLUE); // 设置矩形的颜色

            getChildren().add(rectangle);
        }

        setAlignment(Pos.CENTER);

        setPrefWidth(totalWidth);
        setHeight(20);

        getChildren().add(progressLabel);
    }

    public void setProgress(double progress) {
        if (progress >= 0 && progress <= 1) {
            this.progress = progress;
            updateProgressDisplay();
        }
    }

    private void updateProgressDisplay() {
        int numberOfRectangles = getChildren().size() - 1; // 减去Label
        int filledRectangles = (int) (progress * numberOfRectangles);

        for (int i = 0; i < numberOfRectangles; i++) {
            Rectangle rectangle = (Rectangle) getChildren().get(i);
            if (i < filledRectangles) {
                rectangle.setFill(Color.GREEN); // 已完成的部分颜色
            } else {
                rectangle.setFill(Color.GRAY); // 未完成的部分颜色
            }
        }

        int percentage = (int) (progress * 100);
        progressLabel.setText(percentage + "%");
    }
}