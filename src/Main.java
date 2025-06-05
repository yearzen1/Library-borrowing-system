public class Main {
    public static void main(String[] args) throws Exception{
        Jdatabase.init("root", "wyy041217");//改成自己数据库的密码
        Admin admin = new Admin( "root", "root");
        User user = new User( null, null);
        if(!admin.login())
            Jdatabase.insertAdmin(admin.getName(), admin.getPwd());
            LoginWindow loginwindow=new LoginWindow(user,admin);
            loginwindow.setVisible(true);
    }
}
