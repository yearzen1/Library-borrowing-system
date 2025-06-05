public class Main {
    public static void main(String[] args) throws Exception{
        Jdatabase.init("root", "123456");//改成自己数据库的密码
        Admin admin = new Admin(1, "root", "root");
        User user = new User(0, null, null);
        if(!admin.login())
            Jdatabase.insertAdmin(admin.getId(), admin.getName(), admin.getPwd());
    }
}
