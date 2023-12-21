package com.example.socketcomm.SocketClient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchResultCard extends HBox
{
    String nickName;
    String userID;
    Label userIDText;

    VBox rightBox;

    private boolean isClicked = false; // 新增一个标志来跟踪是否已经点击
    protected static ArrayList<SearchResultCard> allSearchResultCards = new ArrayList<>();


    public SearchResultCard(String name, String ID, VBox rightBox) {
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

        allSearchResultCards.add(this); // 将新创建的对象添加到集合中

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
            boolean isAdded = false;
            for(ContactorCard card: ContactorCard.allContactorCards)
            {
                if (this.userID.equals(card.getUserID()))
                {
                    isAdded = true;
                    break;
                }
            }
            if(!isAdded)
            {
                Button addbutton = new Button("添加" + this.userID);
                addbutton.setStyle("-fx-background-color: #CCCCCC");
                addbutton.setPrefWidth(200);
                addbutton.setPrefHeight(50);

                addbutton.setOnAction(ActionEvent ->
                {
                    //update local friend list
                    ChatWindow.infoCardBox.getChildren().add(new friendInfoCard(this.nickName, this.userID));
                    ChatWindow.friendList.getChildren().add(new ContactorCard(this.nickName, this.userID, ContactorCard.allContactorCards.get(0).rightBox));

                    //transfer add message to friend list server
                    String IP = ChatWindow.serverIP;
                    AtomicInteger loginStatus = new AtomicInteger();

                    try {
                        Socket socket = new Socket(IP, 10001);

                        OutputStream out = socket.getOutputStream();
                        String dataToSend = "add," + ChatWindow.currentUserID + "," + this.userID + "\n"; // 添加换行符
                        out.write(dataToSend.getBytes());
                        out.flush();

                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //transfer confirm message to server
                    chatClient.getCM().send("mesg:", this.getUserID() + "," + ChatWindow.currentUserNickName);

                    rightBox.getChildren().remove(1);
                });

                HBox addBox = new HBox();
                addBox.getChildren().add(addbutton);
                addBox.setPrefWidth(666);
                addBox.setPrefHeight(300);
                addBox.setAlignment(Pos.CENTER);

                if(rightBox.getChildren().size() > 1)
                    rightBox.getChildren().remove(1);
                rightBox.getChildren().add(addBox);
            }
        }

        isClicked = !isClicked; // 切换点击状态

        // 循环处理其他对象
        for (SearchResultCard card : allSearchResultCards) {
            if (card != this) {
                card.setStyle("-fx-background-color: #FFFFFF");
                card.isClicked = false;
            }
        }
    }
}
