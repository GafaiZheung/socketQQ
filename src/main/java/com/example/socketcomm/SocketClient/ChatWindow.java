package com.example.socketcomm.SocketClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class ChatWindow extends Application
{
    public static String serverIP = "127.0.0.1";
    public static ChatTitle chatTitle = new ChatTitle("test");
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


        userIDField.setPromptText("账号");
        passWDField.setPromptText("密码");

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
                    chatClient.getCM().connect(serverIP);
                    FileTransferClient.getFtc().connect(serverIP,8899);
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
//                e.printStackTrace();
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

        Button registerButton = new Button("注册账号");
        Button forgetButton = new Button("忘记密码");

        registerButton.setPrefHeight(40);
        forgetButton.setPrefHeight(40);

        registerButton.setStyle("-fx-background-color: #FFFFFF");
        forgetButton.setStyle("-fx-background-color: #FFFFFF");

        registerButton.setOnMouseClicked(mouseEvent ->
        {
            registerScene(primaryStage);
        });

        forgetButton.setOnMouseClicked(mouseEvent ->
        {
            forgetScene(primaryStage);
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
        primaryStage.setTitle("简易聊天器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 忘记密码界面
    void forgetScene(Stage primaryStage)
    {
        Label focusLabel = new Label("");
        focusLabel.setOpacity(0); // 使Label透明

        TextField uidField = new TextField();
        uidField.setPromptText("账号");
        uidField.setPrefHeight(45);
        uidField.setMaxWidth(240);

        Button confirmButton = new Button("查找");
        confirmButton.setMaxWidth(240);
        confirmButton.setPrefWidth(90);
        confirmButton.setPrefHeight(45);
        confirmButton.setStyle("-fx-background-color: #CCCCCC");

        confirmButton.setOnAction(null);


        confirmButton.setOnAction(actionEvent -> {
            String uid = uidField.getText();

            // 把uid传到服务端，再通过服务端调用Jdbc.checkUid(uid)返回boolean
            // 服务端若收到true则传1，否则传255
            // 传1跳转到confirmScene(uid, primaryStage)，否则提示错误信息
            if (checkIsResist(uid) == 1)
                confirmScene(uid, primaryStage);
            else
            {
                focusLabel.setText("账号不存在");
                focusLabel.setOpacity(1);
            }
        });

        Button backButton = new Button("返回");
        backButton.setMaxWidth(240);
        backButton.setPrefWidth(90);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-background-color: #CCCCCC");
        backButton.setOnMouseClicked(mouseEvent ->
        {
            loginScene(primaryStage);
        });

        HBox bottomBox = new HBox(20);
        bottomBox.getChildren().addAll(backButton, confirmButton);
        bottomBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(focusLabel, uidField, bottomBox);
        vBox.setStyle("-fx-background-color: #FFFFFF");
        vBox.setAlignment(Pos.CENTER);

        uidField.setFocusTraversable(false);
        focusLabel.setFocusTraversable(true);

        Scene scene = new Scene(vBox, 260, 180);

        primaryStage.setTitle("忘记密码");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 身份验证界面
    void confirmScene(String uid, Stage primaryStage)
    {
        Label focusLabel = new Label("");
        focusLabel.setOpacity(0); // 使Label透明

        TextField nickNameField = new TextField();
        nickNameField.setPromptText("昵称");
        nickNameField.setPrefHeight(45);
        nickNameField.setMaxWidth(240);

        TextField phoneNumField = new TextField();
        phoneNumField.setPromptText("手机号");
        phoneNumField.setPrefHeight(45);
        phoneNumField.setMaxWidth(240);

        Button confirmButton = new Button("验证");
        confirmButton.setMaxWidth(240);
        confirmButton.setPrefWidth(90);
        confirmButton.setPrefHeight(45);
        confirmButton.setStyle("-fx-background-color: #CCCCCC");

        confirmButton.setOnAction(null);

        confirmButton.setOnAction(actionEvent -> {
            String nickName = nickNameField.getText();
            String phoneNum = phoneNumField.getText();
            // 把uid, nickName, phoneNum传到服务端，再通过服务端调用Jdbc.checkInfo(uid, nickName, phoneNum)返回boolean
            // 服务端若收到true则传1，否则传255
            // 传1跳转到confirmPasswordScene(uid, primaryStage)，否则提示错误信息

            if (verify(uid, phoneNum) == 1)
                confirmPasswordScene(uid, primaryStage);
            else
            {
                focusLabel.setText("账号信息错误，请重新输入");
                focusLabel.setOpacity(1);
            }
        });

        Button backButton = new Button("返回");
        backButton.setMaxWidth(240);
        backButton.setPrefWidth(90);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-background-color: #CCCCCC");
        backButton.setOnMouseClicked(mouseEvent ->
        {
            forgetScene(primaryStage);
        });

        HBox bottomBox = new HBox(20);
        bottomBox.getChildren().addAll(backButton, confirmButton);
        bottomBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(focusLabel, nickNameField, phoneNumField, bottomBox);
        vBox.setStyle("-fx-background-color: #FFFFFF");
        vBox.setAlignment(Pos.CENTER);

        nickNameField.setFocusTraversable(false);
        focusLabel.setFocusTraversable(true);

        Scene scene = new Scene(vBox, 260, 280);

        primaryStage.setTitle("身份验证");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 重置密码界面
    void confirmPasswordScene(String uid, Stage primaryStage)
    {
        Label focusLabel = new Label("");
        focusLabel.setOpacity(0); // 使Label透明

        TextField passwordField = new PasswordField();
        passwordField.setPromptText("新密码");
        passwordField.setPrefHeight(45);
        passwordField.setMaxWidth(240);

        TextField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("确认密码");
        confirmPasswordField.setPrefHeight(45);
        confirmPasswordField.setMaxWidth(240);

        Button confirmButton = new Button("确认");
        confirmButton.setMaxWidth(240);
        confirmButton.setPrefWidth(90);
        confirmButton.setPrefHeight(45);
        confirmButton.setStyle("-fx-background-color: #CCCCCC");

        confirmButton.setOnAction(null);

        confirmButton.setOnAction(actionEvent -> {
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            // 把传到服务端，再通过服务端调用Jdbc.updatePassword(uid, password)返回boolean
            // 服务端若收到true则传1，否则传255
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
                if(reset(uid, password) == 1)
                {
//                    loginScene(primaryStage);
                    Label finalText = new Label("密码重置成功");

                    Button button = new Button("关闭");
                    button.setPrefHeight(45);
                    button.setPrefWidth(100);
                    button.setStyle("-fx-background-color: #CCCCCC;");

                    VBox box = new VBox(20);
                    box.getChildren().addAll(finalText, button);
                    box.setStyle("-fx-background-color: #FFFFFF");
                    box.setAlignment(Pos.CENTER);

                    button.setOnAction(ActionEvent -> {
                        loginScene(primaryStage);
                    });

                    Scene finalScene = new Scene(box, 200, 100);
                    primaryStage.setScene(finalScene);
                    primaryStage.show();
                }

            }
        });

        Button backButton = new Button("返回");
        backButton.setMaxWidth(240);
        backButton.setPrefWidth(90);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-background-color: #CCCCCC");
        backButton.setOnMouseClicked(mouseEvent ->
        {
            confirmScene(uid, primaryStage);
        });

        HBox bottomBox = new HBox(20);
        bottomBox.getChildren().addAll(backButton, confirmButton);
        bottomBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(focusLabel, passwordField, confirmPasswordField, bottomBox);
        vBox.setStyle("-fx-background-color: #FFFFFF");
        vBox.setAlignment(Pos.CENTER);

        passwordField.setFocusTraversable(false);
        focusLabel.setFocusTraversable(true);

        Scene scene = new Scene(vBox, 260, 280);

        primaryStage.setTitle("重置密码");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    int checkIsResist(String userID)
    {
        String IP = serverIP;

        String checkMsg = "check," + userID;
        AtomicInteger back = new AtomicInteger();

        try {
            Socket socket = new Socket(IP, 8081);

            // 异步发送数据
            Thread sendThread = new Thread(() -> {
                try {
                    OutputStream out = socket.getOutputStream();
                    String dataToSend = checkMsg + "\n"; // 添加换行符
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
                    //这里不要sout测试temp是什么，会变得不幸
                    back.set(in.read());
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
        return back.get();
    }

    int verify(String userID, String phoneNumber)
    {
        String IP = serverIP;

        String checkMsg = "verify," + userID + "," + phoneNumber;
        AtomicInteger back = new AtomicInteger();

        try {
            Socket socket = new Socket(IP, 8081);

            // 异步发送数据
            Thread sendThread = new Thread(() -> {
                try {
                    OutputStream out = socket.getOutputStream();
                    String dataToSend = checkMsg + "\n"; // 添加换行符
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
                    back.set(in.read());
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
        return back.get();
    }


    int reset(String userID, String passWD)
    {
        String IP = serverIP;

        String checkMsg = "reset," + userID + "," + passWD;
        AtomicInteger back = new AtomicInteger();

        try {
            Socket socket = new Socket(IP, 8081);

            // 异步发送数据
            Thread sendThread = new Thread(() -> {
                try {
                    OutputStream out = socket.getOutputStream();
                    String dataToSend = checkMsg + "\n"; // 添加换行符
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
                    back.set(in.read());
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
        return back.get();
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
        confirmButton.setPrefWidth(90);
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

        Button backButton = new Button("返回");
        backButton.setMaxWidth(240);
        backButton.setPrefWidth(90);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-background-color: #CCCCCC");
        backButton.setOnAction(null);
        backButton.setOnMouseClicked(mouseEvent ->
        {
            loginScene(primaryStage);
        });

        HBox bottomBox = new HBox(20);
        bottomBox.getChildren().addAll(backButton, confirmButton);
        bottomBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(topBox, centreBox, confirmField, phoneNumField, bottomBox);
        vBox.setStyle("-fx-background-color: #FFFFFF");
        vBox.setAlignment(Pos.CENTER);

        nickNameField.setFocusTraversable(false);
        focusLabel.setFocusTraversable(true);

        Scene scene = new Scene(vBox, 260, 410);

        primaryStage.setTitle("注册账号");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    String registerAction(String nickName, String password, String phoneNumber)
    {
        String IP = serverIP;

        String registerMsg = "register," + nickName + "," + password + "," + phoneNumber;
        AtomicInteger backUserID = new AtomicInteger();

        try {
            Socket socket = new Socket(IP, 8081);

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
        if(inputTextField.getText() == null)
            return;
        chatMessageField.send(currentUserNickName, inputTextField.getText());
//        chatTextArea.appendText("entered:" + inputTextField.getText() + "\n");
        chatClient.getCM().send("mesg:" ,chatWithID + "," + inputTextField.getText());
        inputTextField.setText("");
    }

    void chatScene(Stage primaryStage)
    {
        Circle userImage = new Circle(24);
        userImage.setFill(Color.rgb(204, 204, 204));

        File file = new File("./icon");

        ImageView chatImageView = new ImageView("file:" + file.getAbsolutePath() + File.separatorChar + "chaT.png");
        chatImageView.setFitHeight(40);
        chatImageView.setFitWidth(38.4);

        ImageView contactImageView = new ImageView("file:" + file.getAbsolutePath() + File.separatorChar + "contact.png");
        contactImageView.setFitWidth(38.4);
        contactImageView.setFitHeight(40);


        ImageView addImageView = new ImageView("file:" + file.getAbsolutePath() + File.separatorChar + "add.png");
        addImageView.setFitHeight(40);
        addImageView.setFitWidth(38.4);

        Label chatText = new Label("消息");
        Label contactText = new Label("联系人");
        Label addText = new Label("加好友");

        VBox chatButtonBox = new VBox();
        chatButtonBox.getChildren().add(chatImageView);
        chatButtonBox.getChildren().add(chatText);
        chatButtonBox.setSpacing(0);
        chatButtonBox.setAlignment(Pos.CENTER);

        VBox contactButtonBox = new VBox();
        contactButtonBox.getChildren().add(contactImageView);
        contactButtonBox.getChildren().add(contactText);
        contactButtonBox.setSpacing(0);
        contactButtonBox.setAlignment(Pos.CENTER);

        VBox addButtonBox = new VBox();
        addButtonBox.getChildren().add(addImageView);
        addButtonBox.getChildren().add(addText);
        addButtonBox.setSpacing(0);
        addButtonBox.setAlignment(Pos.CENTER);

        VBox leftBorderBox = new VBox();
        leftBorderBox.getChildren().add(userImage);
        leftBorderBox.getChildren().add(chatButtonBox);
        leftBorderBox.getChildren().add(contactButtonBox);
        leftBorderBox.getChildren().add(addButtonBox);

        leftBorderBox.setSpacing(20);
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
//        Rectangle functionBar = new Rectangle(666, 40);
        Button sendFileButton = new Button("发送文件");
        sendFileButton.setPrefHeight(30);
        sendFileButton.setPadding(new Insets(0, 0, 0, 10));
        sendFileButton.setStyle("-fx-background-color: #FFFFFF");

        HBox functionBar = new HBox(10);
        functionBar.setPrefHeight(40);
        functionBar.setPrefWidth(666);
        functionBar.getChildren().add(sendFileButton);
        functionBar.setAlignment(Pos.CENTER_LEFT);
        functionBar.setStyle("-fx-background-color: #FFFFFF");

        sendFileButton.setOnMouseClicked(mouseEvent -> openFileChooser(primaryStage));


//        chatTextArea.setPrefWidth(666);
//        chatTextArea.setPrefHeight(360);
//        chatTextArea.setEditable(false);

        inputTextField.setPrefHeight(140);
        inputTextField.setPrefWidth(666);
        inputTextField.setOnAction(this::sendProcess);


        chatScreen.setFill(Color.GRAY);
//        functionBar.setFill(Color.WHITE);

        VBox rightBorderBox = new VBox();
        rightBorderBox.getChildren().add(chatTitle);
        rightBorderBox.getChildren().add(chatMessageField);
//        rightBorderBox.getChildren().add(chatTextArea);
        rightBorderBox.getChildren().add(functionBar);
        rightBorderBox.getChildren().add(inputTextField);


        rightBorderBox.setAlignment(Pos.CENTER);

        chatClient.getCM().send("cnct:" , currentUserID);


        HBox mainBox = new HBox();
        mainBox.getChildren().add(leftBorderBox);
        mainBox.getChildren().add(scrollPane);
        mainBox.getChildren().add(rightBorderBox);

        mainBox.setSpacing(10);
        mainBox.setPadding(new Insets(10,10,0,10));//up right down left
        mainBox.setStyle("-fx-background-color: #FFFFFF");

        Scene scene = new Scene(mainBox, 1020, 620);

        HBox contactBox = new HBox(10);
        contactBox.setStyle("-fx-background-color: #FFFFFF");
        contactBox.setPadding(new Insets(10, 10, 0, 10));

        VBox centreBox = new VBox(20);
        centreBox.setPadding(new Insets(10, 0, 0, 0));

        Button friendNotification = new Button("好友通知                               >");
        friendNotification.setStyle("-fx-background-color: #CCCCCC;-fx-font-size: 14; -fx-alignment: center-right");
        friendNotification.setPrefWidth(265);
        friendNotification.setPrefHeight(40);

        Button friendButton = new Button("好友");
        Button groupButton = new Button("创建群聊");
        friendButton.setStyle("-fx-background-color: #CCCCCC; -fx-font-size: 14");
        groupButton.setStyle("-fx-background-color: #CCCCCC; -fx-font-size: 14");
        friendButton.setPrefHeight(40);
        groupButton.setPrefHeight(40);
        friendButton.setPrefWidth(120);
        groupButton.setPrefWidth(120);

        ChatTitle contactTitle = new ChatTitle("");

        VBox rightBox = new VBox(10);

        groupButton.setOnAction(ActionEvent ->
        {
            contactTitle.setNickName("创建群聊");
            VBox selectBox = new VBox(10);
            for (int i = 0; i < friendInfoCard.allFriendInfoCards.size(); i++)
            {
                selectBox.getChildren().add(new SelectCard(friendInfoCard.allFriendInfoCards.get(i).getNickName(), friendInfoCard.allFriendInfoCards.get(i).getUserID()));
            }
            ScrollPane selectScrollPane = new ScrollPane(selectBox);
            selectScrollPane.setPrefWidth(666);
            selectScrollPane.setPrefHeight(600);


            Button createGroupButton = new Button("确认创建群聊");
            createGroupButton.setPrefWidth(400);
            createGroupButton.setPrefHeight(45);
            createGroupButton.setStyle("-fx-background-color: #CCCCCC;");

            VBox createGroupButtonBox = new VBox();
            createGroupButtonBox.setPrefWidth(666);
            createGroupButtonBox.setPrefHeight(100);
            createGroupButtonBox.getChildren().add(createGroupButton);
            createGroupButtonBox.setAlignment(Pos.CENTER);


            rightBox.getChildren().clear();
            rightBox.getChildren().addAll(contactTitle, selectBox, createGroupButtonBox);
        });

        HBox centreButtonBox = new HBox(25);
        centreButtonBox.getChildren().addAll(friendButton, groupButton);

        ScrollPane friendScrollPane = new ScrollPane();

        centreBox.getChildren().addAll(friendNotification, centreButtonBox, friendScrollPane);
        centreBox.setAlignment(Pos.TOP_CENTER);
        centreBox.setPrefWidth(265);


        friendNotification.setOnAction(ActionEvent ->
        {
            contactTitle.setNickName("好友通知");
            ScrollPane friendNotificationPane = new ScrollPane();
            rightBox.getChildren().clear();
            rightBox.getChildren().addAll(contactTitle,friendNotificationPane);
        });

        contactBox.getChildren().addAll(centreBox, rightBox);


        HBox addBox = new HBox(10);
        addBox.setStyle("-fx-background-color: #FFFFFF");
        addBox.setPadding(new Insets(10, 10, 0, 10));

        TextField searchField = new TextField();
        searchField.setPromptText("账号/昵称");
        searchField.setPrefWidth(265);
        searchField.setPrefHeight(40);

        Label focusLabel = new Label("");
        focusLabel.setOpacity(0);
        focusLabel.setFocusTraversable(true);
        searchField.setFocusTraversable(false);

        ScrollPane searchResult = new ScrollPane();

        VBox centreBoxA = new VBox(10);
        centreBoxA.setPadding(new Insets(10, 0, 0, 0));
        centreBoxA.setAlignment(Pos.TOP_CENTER);
        centreBoxA.setPrefWidth(265);
        centreBoxA.getChildren().addAll(searchField, focusLabel, searchResult);

        addBox.getChildren().addAll(centreBoxA);

        chatImageView.setOnMouseClicked(mouseEvent ->
        {
            if(scene.getRoot().equals(mainBox))
                return;

            mainBox.getChildren().add(0, leftBorderBox);
            scene.setRoot(mainBox);
        });

        contactImageView.setOnMouseClicked(mouseEvent ->
        {
            if(scene.getRoot().equals(contactBox))
                return;

            contactBox.getChildren().add(0, leftBorderBox);
            scene.setRoot(contactBox);
        });

        addImageView.setOnMouseClicked(mouseEvent ->
        {
//            addFriendScene(primaryStage);
            if(scene.getRoot().equals(addBox))
                return;

            addBox.getChildren().add(0, leftBorderBox);
            scene.setRoot(addBox);
        });

        primaryStage.setTitle("Chat Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void addFriendScene(Stage primaryStage)
    {
        Label focusLabel = new Label("");
        focusLabel.setOpacity(0); // 使Label透明

        TextField addField = new TextField();
        addField.setPromptText("账号/昵称");
        addField.setPrefHeight(45);
        addField.setMaxWidth(240);

        Button confirmButton = new Button("查找");
        confirmButton.setMaxWidth(240);
        confirmButton.setPrefWidth(90);
        confirmButton.setPrefHeight(45);
        confirmButton.setStyle("-fx-background-color: #CCCCCC");

        confirmButton.setOnAction(null);
        confirmButton.setOnAction(actionEvent -> {
            String message = addField.getText();
            // 把message传入服务端，然后由服务端调用数据库查找，返回Arraylist<String>
            // 格式为”UserID,NickName“   message指的是(输入账号/昵称)



        });

        Button backButton = new Button("返回");
        backButton.setMaxWidth(240);
        backButton.setPrefWidth(90);
        backButton.setPrefHeight(45);
        backButton.setStyle("-fx-background-color: #CCCCCC");
        backButton.setOnMouseClicked(mouseEvent ->
        {
            chatScene(primaryStage);
        });

        HBox bottomBox = new HBox(20);
        bottomBox.getChildren().addAll(backButton, confirmButton);
        bottomBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(focusLabel, addField, bottomBox);
        vBox.setStyle("-fx-background-color: #FFFFFF");
        vBox.setAlignment(Pos.CENTER);

        addField.setFocusTraversable(false);
        focusLabel.setFocusTraversable(true);

        Scene scene = new Scene(vBox, 260, 180);

        primaryStage.setTitle("添加好友");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFileChooser(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File");

        // 设置文件类型过滤器（可选）
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // 显示文件选择对话框
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            System.out.println("Selected File: " + filePath);
            chatMessageField.sendFile(currentUserNickName, selectedFile.getName(), fileSizeFormatter(selectedFile.length()), selectedFile.getAbsolutePath());
            try {
                FileTransferClient.getFtc().sendFile(filePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 在这里你可以处理选择的文件，如读取、保存等操作
        } else {
            System.out.println("No file selected.");
        }
    }

    private String fileSizeFormatter(long fileSize)
    {
        String[] sizeUnits = {"Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int unitIndex = 0;

        while (fileSize > 1024 && unitIndex < sizeUnits.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }

        return String.format("%d %s", fileSize, sizeUnits[unitIndex]);
    }




    private int loginAction(ActionEvent event) throws IOException, InterruptedException {
        String IP = serverIP;
        String userID = userIDField.getText();
        String passWD = passWDField.getText();
        String userPWD = "login," + userID + "," + passWD;
        AtomicInteger loginStatus = new AtomicInteger();

        try {
            Socket socket = new Socket(IP, 8081);

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
        String IP = serverIP;

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
