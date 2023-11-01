package com.example.socketcomm.SocketClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class ChatWindow extends Application
{
    public static chatTitle chatTitle = new chatTitle("test");
    public static MessageField chatMessageField = new MessageField();
    public static TextArea chatTextArea = new TextArea();
    public static TextField inputTextField = new TextField();
    public static TextField userIDField = new TextField();
    public static TextField passWDField = new TextField();
    public static String chatWithID;
    protected static String currentUserID;
    protected static String currentUserNickName;


    @Override
    public void start(Stage stage) throws Exception
    {
        loginScene(stage);
    }

    void loginScene(Stage primaryStage)
    {
        Circle loginUserImage = new Circle(60);
        loginUserImage.setFill(Color.rgb(204, 204, 204));

        VBox loginUserImageBox = new VBox();
        loginUserImageBox.getChildren().add(loginUserImage);
        loginUserImageBox.setAlignment(Pos.BOTTOM_CENTER);
        loginUserImageBox.setPrefHeight(140);

        Label focusLabel = new Label("");
        focusLabel.setFocusTraversable(true);
        focusLabel.setOpacity(0); // 使Label透明


        userIDField.setPromptText("userID");
        passWDField.setPromptText("password");

        passWDField.setOnAction(null);

        userIDField.setPrefHeight(40);
        passWDField.setPrefHeight(40);

        userIDField.setMaxWidth(200);
        passWDField.setMaxWidth(200);


        passWDField.setOnAction(actionEvent -> {
            if (currentUserID != null) {
                // 用户已登录，不执行任何操作
                return;
            }
            try
            {
                currentUserID = userIDField.getText();
                //                System.out.println(loginAction(actionEvent));
                if(loginAction(actionEvent) == 1)
                {
                    chatClient.getCM().connect("127.0.0.1");
                    chatScene(primaryStage);
                }
                else if (loginAction(actionEvent) == 255)
                {
                    focusLabel.setFocusTraversable(false);
                    focusLabel.setOpacity(1);
                    focusLabel.setText("账号或密码错误，请重新输入");
                    userIDField.setText("");
                    passWDField.setText("");
                    currentUserID = null;
                }
            }catch (RuntimeException e)
            {
                focusLabel.setFocusTraversable(false);
                focusLabel.setOpacity(1);
                focusLabel.setText("无法连接到登录服务器");
                currentUserID = null;
            }
            catch (IOException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        });

        VBox loginTextBox = new VBox();
        loginTextBox.getChildren().addAll(focusLabel, userIDField, passWDField);
        loginTextBox.setAlignment(Pos.CENTER);
        loginTextBox.setSpacing(30);
        loginTextBox.setPrefHeight(150);
        loginTextBox.setPrefWidth(250);

        Button registerButton = new Button("register");
        Button forgetButton = new Button("forget passwd?");

        registerButton.setPrefHeight(40);
        forgetButton.setPrefHeight(40);

        registerButton.setStyle("-fx-background-color: #FFFFFF");
        forgetButton.setStyle("-fx-background-color: #FFFFFF");

        registerButton.setOnMouseClicked(mouseEvent ->
        {
            registerScene(primaryStage);
        });


        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(30);
        buttonBox.getChildren().addAll(registerButton, forgetButton);
        buttonBox.setPrefHeight(50);


        VBox loginWindowBox = new VBox();
        loginWindowBox.getChildren().addAll(loginUserImageBox, loginTextBox, buttonBox);
        loginWindowBox.setStyle("-fx-background-color: #FFFFFF");

        Scene scene = new Scene(loginWindowBox, 250, 340);
        primaryStage.setTitle("register & login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void forgetButton(Stage primaryStage)
    {
        ;
    }

    void registerScene(Stage primaryStage)
    {
        Circle userImage = new Circle(45);
        userImage.setFill(Color.rgb(204, 204, 204));

        Label focusLabel = new Label("");
        focusLabel.setOpacity(0); // 使Label透明

        TextField nickNameField = new TextField();
        nickNameField.setPromptText("昵称");
        nickNameField.setPrefHeight(45);
        nickNameField.setMaxWidth(130);

        HBox topBox = new HBox(20);
        topBox.getChildren().addAll(userImage, nickNameField);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPrefHeight(90);

        TextField passwordField = new TextField();
        passwordField.setPromptText("密码");
        passwordField.setPrefHeight(45);
        passwordField.setMaxWidth(240);

        VBox centreBox = new VBox();
        centreBox.setAlignment(Pos.CENTER);
        centreBox.getChildren().addAll(focusLabel, passwordField);

        TextField confirmField = new TextField();
        confirmField.setPromptText("确认密码");
        confirmField.setPrefHeight(45);
        confirmField.setMaxWidth(240);

        TextField phoneNumField = new TextField();
        phoneNumField.setPromptText("手机号");
        phoneNumField.setPrefHeight(45);
        phoneNumField.setMaxWidth(240);

        Button confirmButton = new Button("确认");
        confirmButton.setPrefHeight(45);
        confirmButton.setMaxWidth(240);
        confirmButton.setStyle("-fx-background-color: #CCCCCC");

        confirmButton.setOnAction(null);
        
        confirmButton.setOnAction(actionEvent -> {
            String password = passwordField.getText();
            String confirmPassword =  confirmField.getText();

            if (password == null)
            {
                focusLabel.setText("请输入密码");
                focusLabel.setOpacity(1);
            }
            else if (confirmPassword == null)
            {
                focusLabel.setText("请输入确认密码");
                focusLabel.setOpacity(1);
            }
            else if (!password.equals(confirmPassword))
            {
                focusLabel.setText("密码与确认密码不符");
                focusLabel.setOpacity(1);
            }
            else
            {
                String backUserID = registerAction(nickNameField.getText(), password, phoneNumField.getText());
                Label backIDText = new Label("您的账号是：" + backUserID);
                Button button = new Button("立即登录");

                VBox success = new VBox(30);
                success.getChildren().addAll(backIDText, button);
                success.setStyle("-fx-background-color: #FFFFFF");
                success.setAlignment(Pos.CENTER);

                Scene successScene = new Scene(success, 260, 100);

                button.setOnAction(actionEvent1 -> {
                    userIDField.setText(backUserID);
                    passWDField.setFocusTraversable(true);
                    loginScene(primaryStage);
                });
                primaryStage.setScene(successScene);
                primaryStage.show();
            }
        });

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(topBox, centreBox, confirmField, phoneNumField, confirmButton);
        vBox.setStyle("-fx-background-color: #FFFFFF");
        vBox.setAlignment(Pos.CENTER);

        nickNameField.setFocusTraversable(false);
        focusLabel.setFocusTraversable(true);


        Scene scene = new Scene(vBox, 260, 410);

        primaryStage.setTitle("register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    String registerAction(String nickName, String password, String phoneNumber)
    {
        String IP = "127.0.0.1";

        String registerMsg = "register," + nickName + "," + password + "," + phoneNumber;
        AtomicInteger backUserID = new AtomicInteger();

        try {
            Socket socket = new Socket(IP, 8080);

            // 异步发送数据
            Thread sendThread = new Thread(() -> {
                 try {
                    OutputStream out = socket.getOutputStream();
                    String dataToSend = registerMsg + "\n"; // 添加换行符
                    out.write(dataToSend.getBytes());
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // 异步接收数据
            Thread receiveThread = new Thread(() -> {
                try {
                    InputStream in = socket.getInputStream();
                    String temp = readData(in);
                    //这里不要sout测试temp是什么，会变得不幸
                    backUserID.set(Integer.parseInt(temp));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sendThread.start();
            receiveThread.start();

            // 等待异步线程完成
            sendThread.join();
            receiveThread.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(backUserID.get());
    }


    void sendProcess(ActionEvent event)
    {
        chatMessageField.send(currentUserNickName, inputTextField.getText());
//        chatTextArea.appendText("entered:" + inputTextField.getText() + "\n");
        chatClient.getCM().send("msg," + chatWithID + "," + inputTextField.getText());
        inputTextField.setText("");
    }

    void chatScene(Stage primaryStage)
    {
        Circle userImage = new Circle(24);
        Ellipse chatButton = new Ellipse(24, 13);
        Ellipse contactButton = new Ellipse(24, 13);

        userImage.setFill(Color.rgb(204, 204, 204));
        chatButton.setFill(Color.rgb(204, 204, 204));
        contactButton.setFill(Color.rgb(204, 204, 204));

        Label chatText = new Label("chat");
        Label contactText = new Label("contact");

        VBox chatButtonBox = new VBox();
        chatButtonBox.getChildren().add(chatButton);
        chatButtonBox.getChildren().add(chatText);
        chatButtonBox.setSpacing(10);
        chatButtonBox.setAlignment(Pos.CENTER);

        VBox contactButtonBox = new VBox();
        contactButtonBox.getChildren().add(contactButton);
        contactButtonBox.getChildren().add(contactText);
        contactButtonBox.setSpacing(10);
        contactButtonBox.setAlignment(Pos.CENTER);

        VBox leftBorderBox = new VBox();
        leftBorderBox.getChildren().add(userImage);
        leftBorderBox.getChildren().add(chatButtonBox);
        leftBorderBox.getChildren().add(contactButtonBox);

        leftBorderBox.setSpacing(25);
        leftBorderBox.setAlignment(Pos.TOP_CENTER);
        leftBorderBox.setLayoutY(10);
        leftBorderBox.setStyle("-fx-background-color: #FFFFFF");
        leftBorderBox.setPrefWidth(52);

        ScrollPane scrollPane = new ScrollPane();
        VBox infoCardBox = new VBox(10);

        //根据currentUserID创建好友列表
        if (!Objects.equals(currentUserID, "0")) {
            try {
                createFriendList(infoCardBox);
            }catch (RuntimeException e)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText(null);
                alert.setContentText("服务器内部错误，点击确认退出客户端");

                ButtonType confirmButton = new ButtonType("确认");
                alert.getButtonTypes().setAll(confirmButton);

                alert.showAndWait().ifPresent(response -> {
                    if (response == confirmButton) {
                        try {
                            Platform.exit();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
            }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        friendInfoCard card1 = new friendInfoCard("gafai","10001");
//        friendInfoCard card2 = new friendInfoCard("xq","10002");
        scrollPane.setContent(infoCardBox);

        Rectangle chatScreen = new Rectangle(666, 360);
        Rectangle functionBar = new Rectangle(666, 40);

//        chatTextArea.setPrefWidth(666);
//        chatTextArea.setPrefHeight(360);
//        chatTextArea.setEditable(false);

        inputTextField.setPrefHeight(140);
        inputTextField.setPrefWidth(666);
        inputTextField.setOnAction(this::sendProcess);


        chatScreen.setFill(Color.GRAY);
        functionBar.setFill(Color.WHITE);

        VBox rightBorderBox = new VBox();
        rightBorderBox.getChildren().add(chatTitle);
        rightBorderBox.getChildren().add(chatMessageField);
//        rightBorderBox.getChildren().add(chatTextArea);
        rightBorderBox.getChildren().add(functionBar);
        rightBorderBox.getChildren().add(inputTextField);


        rightBorderBox.setAlignment(Pos.CENTER);

        chatClient.getCM().send("connect," + currentUserID);


        HBox mainBox = new HBox();
        mainBox.getChildren().add(leftBorderBox);
        mainBox.getChildren().add(scrollPane);
        mainBox.getChildren().add(rightBorderBox);

        mainBox.setSpacing(10);
        mainBox.setPadding(new Insets(10,10,0,10));//up right down left
        mainBox.setStyle("-fx-background-color: #FFFFFF");

        Scene scene = new Scene(mainBox, 1020, 620);
        primaryStage.setTitle("Chat Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int loginAction(ActionEvent event) throws IOException, InterruptedException {
        String IP = "127.0.0.1";
        String userID = userIDField.getText();
        String passWD = passWDField.getText();
        String userPWD = "login," + userID + "," + passWD;
        AtomicInteger loginStatus = new AtomicInteger();

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
                    throw new RuntimeException(e);
                }
            });

            // 异步接收数据
            Thread receiveThread = new Thread(() -> {
                try {
                    InputStream in = socket.getInputStream();
                    loginStatus.set(in.read());
//                    System.out.println("Login Status: " + loginStatus);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sendThread.start();
            receiveThread.start();

            // 等待异步线程完成
            sendThread.join();
            receiveThread.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return loginStatus.get();
    }

    private void createFriendList(VBox friendListBox) throws IOException, InterruptedException
    {
        String IP = "127.0.0.1";

        try {
            Socket socket = new Socket(IP, 10001);

            // 异步发送数据
            Thread sendThread = new Thread(() -> {
                try {
                    OutputStream out = socket.getOutputStream();
                    String dataToSend = currentUserID + "\n"; // 添加换行符
                    out.write(dataToSend.getBytes());
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // 异步接收数据
            Thread receiveThread = new Thread(() -> {
                try {
                    BufferedReader dataInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true)
                    {
                        String info = dataInput.readLine();
                        if (info == null || info.equals("end"))
                        {
                            break;
                        }
                        else
                        {
                            String[] strInfo = info.split(",");
                            if(strInfo[1].equals(currentUserID))
                                currentUserNickName = strInfo[0];
                            friendInfoCard card = new friendInfoCard(strInfo[0], strInfo[1]);
                            friendListBox.getChildren().add(card);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            sendThread.start();
            receiveThread.start();

            // 等待异步线程完成
            sendThread.join();
            receiveThread.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static String readData(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return br.readLine();  // 从客户端读取一行数据
    }

    public static void main(String[] args)
    {
        launch();
    }

}
