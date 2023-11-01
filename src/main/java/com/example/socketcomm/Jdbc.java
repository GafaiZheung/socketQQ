package com.example.socketcomm;

import java.lang.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Jdbc {

    public static Connection con;
    public static String url = "jdbc:mysql://localhost:3306/test_qq?useSSL=false";
    public static String user = "root";
    public static String password = "20031016LCx.";
    public static Statement statement;
    
    // 加载驱动
    public Jdbc() {
        // 加载mysql的jdbc
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("成功加载驱动！");
        }
        catch (Exception e) {
            System.out.println("加载驱动失败！");
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(url, user, password);
            if(!con.isClosed()) System.out.println("成功连接数据库");
            statement = con.createStatement();
            ResultSet set = con.getMetaData().getTables(null, null, "foundation", null);
            if(!set.next()) {
                String sql = "CREATE TABLE foundation (\n" +
                        "  UserID varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                        "  Password varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                        "  NickName varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,\n" +
                        "  Phone varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,\n" +
                        "  PRIMARY KEY (UserID, Password) USING BTREE\n" +
                        ")";
                int rs = statement.executeUpdate(sql);
                if (rs > 0) {
                    System.out.println("更新成功");
                }
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
    }

    // 登录检查号码
    public boolean check_number(String uid, String passwd) {
        try {
            String sql = "select * from foundation";  // 定义sql语句
            ResultSet rs = statement.executeQuery(sql);  // 查询数据库
            while(rs.next()) {
                String id = rs.getString("UserID");
                String pass = rs.getString("Password");
                if(uid.equals(id) && passwd.equals(pass)) return true;
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
        return false;
    }

    // 遍历好友
    public ArrayList<String> select_friend(String uid) {
        int cnt = 0;
        try {
            String sql = "select * from f_" + uid;  // 定义sql语句
            ResultSet rs = statement.executeQuery(sql);  // 查询数据库
            while(rs.next()) {
                cnt++;
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
        System.out.println(cnt);
        ArrayList<String> s = new ArrayList<String>();
        cnt = 0;
        try {
            String sql = "select * from f_" + uid;  // 定义sql语句
            ResultSet rs = statement.executeQuery(sql);  // 查询数据库
            while(rs.next()) {
                s.add(rs.getString("NickName") + "," + rs.getString("UserID") + "\n");
                cnt++;
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
        return s;
    }

    // 注册后更新信息并创建表
    public void update_qq(String UserID, String NickName, String PassWord, String Phonenumber) {
        try {
            String sql = "insert into foundation values ('" + UserID + "', '" + PassWord + "', '" + NickName + "', '" + Phonenumber + "')";  // 定义sql语句
            int rs = statement.executeUpdate(sql);  // 查询数据库
            String sql1 = "CREATE TABLE f_" + UserID + "  (\n" +
                    "  UserID varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                    "  NickName varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,\n" +
                    "  Phone varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,\n" +
                    "  PRIMARY KEY (UserID) USING BTREE\n" +
                    ")";
            int rs1 = statement.executeUpdate(sql1);
            String sql2 = "insert into f_" + UserID + " values ('" + UserID + "', '" + NickName + "', '" + Phonenumber + "')";
            int rs2 = statement.executeUpdate(sql2);
            if(rs > 0 && rs1 > 0 && rs2 > 0) {
                System.out.println("更新成功");
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
    }

    // 查找好友是否已经注册
    public static boolean check_friend(String FriendID) {
        boolean b = false;
        try {
            String sql1 = "select * from foundation where UserID = " + FriendID;
            ResultSet rs1 = statement.executeQuery(sql1);
            String password = new String();
            password = null;
            while(rs1.next()) {
                password = rs1.getString("Password");
            }
            if(password != null) b = true;
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
        return b;
    }

    // 添加好友
    public void add_friend(String UserID, String FriendID) {
        try {
            String sql1 = "select * from foundation where UserID = " + FriendID;
            ResultSet rs1 = statement.executeQuery(sql1);
            String Friend_NickName = new String();
            String Phonenumber = new String();
            while(rs1.next()) {
                Friend_NickName = rs1.getString("NickName");
                Phonenumber = rs1.getString("Phone");
            }
            String sql = "insert into f_" + UserID + " values ('" + FriendID + "', '" + Friend_NickName + "', '" + Phonenumber + "')"; // 定义sql语句
            int rs = statement.executeUpdate(sql);  // 查询数据库
            if(rs > 0) {
                System.out.println("更新成功");
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
    }

    // 删除好友
    public void delete_friend(String UserID, String FriendID) {
        try {
            String sql = "delete from f_" + UserID + " where UserID = \"" + FriendID + "\"";
            int rs = statement.executeUpdate(sql);  // 查询数据库
            if(rs > 0) {
                System.out.println("更新成功");
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
    }

    // 初始化（发信人 + 收信人）表
    public void Initialize(String SendID, String ReceiverID) {
        try {
            ResultSet set = con.getMetaData().getTables(null, null, "f_" + SendID + "_" + ReceiverID, null);
            if(!set.next()) {
                String sql = "CREATE TABLE f_" + SendID + "_" + ReceiverID + "  (\n" +
                        "  status varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                        "  receiverNickName varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,\n" +
                        "  message varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,\n" +
                        "  number bigint(0) NOT NULL,\n" +
                        "  PRIMARY KEY (number) USING BTREE\n" +
                        ")";
                int rs = statement.executeUpdate(sql);  // 查询数据库
                System.out.println(rs);
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
    }

    // 获得该表中的元素
    public ArrayList<String> get_message(String UserID, String ReceiverID) {
        ArrayList<String> As = new ArrayList<String>();
        String s = new String();
        try {
            String sql = "select * from f_" + UserID + "_" + ReceiverID;
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                s = "";
                s += rs.getString("status");
                s += "," + rs.getString("receiverNickName");
                s += "," + rs.getString("message");
                As.add(s);
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
        return As;
    }

    // 把元素写入该表中
    public void set_message(String status, String SendID, String ReceiverID, String message) {
        try {
            int number = 0;
            // 通过ID找NickName
            String sql1 = "select nickname from foundation where userid = \"" + ReceiverID + "\"";
            ResultSet res = statement.executeQuery(sql1);
            String NickName = new String();
            while(res.next()) {
                NickName = res.getString("nickname");
            }
            // 通过发送条数顺序设置主键
            String sql2 = "select * from f_" + SendID + "_" + ReceiverID;
            res = statement.executeQuery(sql2);
            while(res.next()) {
                number++;
            }
            // 插入信息
            String sql = "insert into f_" + SendID + "_" + ReceiverID + " values ('" + status + "', '" + NickName + "', '" + message + "', " + number +  ")";
            int rs = statement.executeUpdate(sql);  // 查询数据库
            if(rs > 0) {
                System.out.println("更新成功");
            }
        }
        catch (Exception e) {
            System.out.println("获取信息错误！");
            e.printStackTrace();
        }
    }

    public void create_troop(String UserID) {

    }
}


