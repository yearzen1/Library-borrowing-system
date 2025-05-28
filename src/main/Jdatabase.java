package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
public class Jdatabase {

    private static String createdatabase = "create database if not exists library_borrow_system";
    private static String createStudenttalbe = "create table if not EXISTS library_borrow_system.student  (sname CHAR(6) not NULL, sno char(10) not null PRIMARY key, spassword char(6) not null);";
    private static PreparedStatement preparedStatement = null;
    private static Connection connection = null;
    static void init(String user,String pwd) throws Exception //数据库账号 密码
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/mysql?serverTimezone=Asia/Shanghai";
        Connection connection = null; 
        if((connection = DriverManager.getConnection(url, user, pwd)) == null)
            System.out.println("error");
        else
            System.out.println("success");
        preparedStatement = connection.prepareStatement(createdatabase);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createStudenttalbe);
        preparedStatement.executeUpdate();
    }

}
