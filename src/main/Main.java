package main;
public class Main {
    public static void main(String[] args) throws Exception{
        Jdatabase.init("root", "123456");//改成自己数据库的密码
        LoginWindow loginWindow = new LoginWindow();
    }
}
