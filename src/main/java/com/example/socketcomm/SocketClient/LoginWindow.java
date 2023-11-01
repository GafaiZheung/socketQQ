package com.example.socketcomm.SocketClient;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class LoginWindow extends Application
{
    TextField fieldID;
    TextField fieldWD;
    Button registerButton;
    Button loginButton;

    public void start(Stage stage)
    {
        GridPane gridPane = new GridPane();

        Label label1 = new Label("userID");
        Label label2 = new Label("passWD");

        fieldID = new TextField();
        fieldWD = new TextField();

        registerButton = new Button("register");
        registerButton.setOnAction(this::registerAction);

        loginButton = new Button("login");
        loginButton.setOnAction(actionEvent ->
        {
            try
            {
                loginAction(actionEvent);
            } catch (IOException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().add(registerButton);
        hBox.getChildren().add(loginButton);
        hBox.setSpacing(30);
        hBox.setAlignment(Pos.CENTER);

        gridPane.add(label1, 0, 0);
        gridPane.add(fieldID, 1,0);
        gridPane.add(label2, 0, 1);
        gridPane.add(fieldWD,1, 1);
        gridPane.add(hBox, 1, 2);

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(15);


        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(gridPane);
        flowPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(flowPane, 400, 200);

        stage.setTitle("userLogin");
        stage.setScene(scene);
        stage.show();
    }

    private void registerAction(ActionEvent event)
    {
     ;
    }

    private void loginAction(ActionEvent event) throws IOException, InterruptedException {
        String IP = "127.0.0.1";
        String userID = fieldID.getText();
        String passWD = fieldWD.getText();
        String userPWD = userID + "," + passWD;

        try {
            Socket socket = new Socket(IP, 8080);

            // 异步发送数据
            Thread sendThread = new Thread(() -> {
                try {
                    OutputStream out = socket.getOutputStream();
                    String dataToSend = userPWD + "\n"; // 添加换行符
                    out.write(dataToSend.getBytes());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 异步接收数据
            Thread receiveThread = new Thread(() -> {
                try {
                    InputStream in = socket.getInputStream();
                    int loginStatus = in.read();
                    System.out.println("Login Status: " + loginStatus);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            sendThread.start();
            receiveThread.start();

            // 等待异步线程完成
            sendThread.join();
            receiveThread.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }


}
