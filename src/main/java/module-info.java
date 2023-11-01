module com.example.socketcomm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.socketcomm.SocketClient to javafx.fxml;
    exports com.example.socketcomm.SocketClient;

    opens com.example.socketcomm.SocketServer to javafx.fxml;
    exports com.example.socketcomm.SocketServer;
}