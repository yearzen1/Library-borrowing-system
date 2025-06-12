import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.Date;
public class User {

    // 构造方法
    User(String name,String pwd)
    {
        this.name = name;
        this.pwd = pwd;
    }
    public User() {
        //TODO Auto-generated constructor stub
    }
    private int id;
    private String name; 
    private String pwd;
    
    // 设置用户姓名
    public void setName(String name) {
        this.name = name;
    }
    // 设置用户密码
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    // 获取用户ID
    public int getId() {
        return id;
    }
    // 获取用户姓名
    public String getName() {
        return name;
    }
    // 获取用户密码
    public String getPwd() {
        return pwd;
    }
    // 用户注册
    Boolean register() throws Exception
    {
        // 检查用户信息是否符合要求，若ID不存在则注册新用户
        ResultSet temp = Jdatabase.selectUserByName(name);
        if(!temp.next())
        {
            Jdatabase.insertUser(name, pwd);
            temp = Jdatabase.selectUserByName(name);
            temp.next();
            id = temp.getInt(1);
            return true;
        }
        else
            return false;
    }
    // 用户登录
    Boolean login() throws Exception
    {
        // 检查用户ID和姓名是否匹配
        ResultSet temp = Jdatabase.selectUserByName(name);
        if(temp.next() && temp.getString(3).equals(pwd))
        {
            id = temp.getInt(1);
            return true;
        }
        else
            return false;
    }
    // 修改用户姓名
    void changeName(String name) throws Exception
    {   
        // 修改用户姓名，长度不能超过50
        Jdatabase.updateUser(id, name, pwd);
        this.name = name;
    }
    // 修改用户密码
    void changePwd(String pwd) throws Exception
    {
        // 修改用户密码，长度不能超过100
        Jdatabase.updateUser(id, name, pwd);
        this.pwd = pwd;
    }
    // 查看所有图书
    ArrayList<String[]> viewAllBooks() throws Exception
    {
        // 返回所有图书的信息列表
        ResultSet temp = Jdatabase.selectAllBooks();
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        String[] strarray = null;
        while(temp.next())
        {
            strarray = new String[tempdata.getColumnCount()];
            for(int i=0;i<=strarray.length-1;++i)
                strarray[i] = temp.getString(i+1);
            array.add(strarray);
        }
        return array;
    }
    // 查看特定书名的图书
    ArrayList<String[]> viewSpecificBook(String name) throws Exception
    {
        // 根据书名搜索图书
        ResultSet temp = Jdatabase.selectBooksByTitle(name);
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        String[] strarray = null;
        while(temp.next())
        {
            strarray = new String[tempdata.getColumnCount()];
            for(int i=0;i<=strarray.length-1;++i)
                strarray[i] = temp.getString(i+1);
            array.add(strarray);
        }
        return array;
    }
    // 查看可借阅的图书
    ArrayList<String[]> viewAviliableBooks() throws Exception
    {
        // 返回所有可借阅的图书列表
        ResultSet temp = Jdatabase.selectAvailableBooks();
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        String[] strarray = null;
        while(temp.next())
        {
            strarray = new String[tempdata.getColumnCount()];
            for(int i=0;i<=strarray.length-1;++i)
                strarray[i] = temp.getString(i+1);
            array.add(strarray);
        }
        return array;
    }
    // 查看我的借阅记录
    ArrayList<String[]> viewMyBorrows() throws Exception
    {
        // 自动更新逾期状态后返回当前用户的借阅记录
        autoUpdateOverdueBorrows();
        ResultSet temp = Jdatabase.selectBorrowsByUser(this.id);
        if(!temp.next())
            return null;
            
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        
        while(temp.next())
        {
            String[] strarray = new String[tempdata.getColumnCount()];
            for(int i=0; i<=strarray.length-1; ++i)
                strarray[i] = temp.getString(i+1);
            array.add(strarray);
        }
        return array;
    }
    // 借阅图书
    Boolean borrowBooks(int id) throws Exception
    {
        // 检查图书是否可借，若可借则创建借阅记录
        ResultSet temp =Jdatabase.selectBookById(id);
        if(!temp.next())
            return false;
        Boolean isborrow = temp.getBoolean("isBorrow");
        if(isborrow == true)
            return false;
        Jdatabase.updateBookStatus(id, true);
        Date borrowDate = new java.sql.Date(System.currentTimeMillis());
        Date dueDate = new java.sql.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30天后到期
        Jdatabase.insertBorrow(this.id, id, borrowDate, dueDate, null, "未归还");
        return true;

    }
    // 自动检查并更新逾期借阅记录
    private void autoUpdateOverdueBorrows() throws Exception {
        // 检查当前用户的借阅记录，若已逾期则更新状态
        ResultSet temp = Jdatabase.selectBorrowsByUser(this.id);
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        while(temp.next()) {
            String status = temp.getString("status");
            java.sql.Date dueDate = temp.getDate("duedate");
            int borrowid = temp.getInt("borrowid");
            if("未归还".equals(status) && dueDate != null && dueDate.before(currentDate)) {
                // 逾期未归还，自动更新状态
                Jdatabase.updateBorrowStatus(borrowid, "逾期未归还");
            }
        }
    }

     public Boolean renewBook(int bookId) throws Exception {
        // 1. 找到当前用户该书未归还的借阅记录
        ResultSet rs = Jdatabase.selectBorrowsByUser(this.id);
        Date now = new Date(System.currentTimeMillis());
        while (rs.next()) {
            int bid = rs.getInt("bookid");
            String status = rs.getString("status");
            Date due = rs.getDate("duedate");
            int borrowId = rs.getInt("borrowid");
            // 只对“未归还”且未逾期的记录允许续借
            if (bid == bookId && "未归还".equals(status) && due != null && !due.before(now)) {
                // 2. 计算新的到期日期 +30天
                Date newDue = new Date(due.getTime() + 30L * 24 * 60 * 60 * 1000);
                // 3. 更新借阅记录的到期日期
                Jdatabase.updateBorrowDueDate(borrowId, newDue);
                return true;
            }
        }
        return false;
    }

    // 归还图书
    Boolean returnBooks(int id) throws Exception
    {
        // 查找用户的借阅记录并处理归还，根据是否逾期更新不同状态
        ResultSet temp = Jdatabase.selectBorrowsByUser(this.id);
        int borrowid = -1;
        java.sql.Date dueDate = null;
        Boolean isBooks = false;
        while(temp.next()) {
            if(temp.getInt("bookid") == id && 
            (temp.getString("status").equals("未归还") || temp.getString("status").equals("逾期未归还"))) {
                borrowid = temp.getInt("borrowid");
                dueDate = temp.getDate("duedate");
                isBooks = true;
                break;
            }
        }
        if(!isBooks)
            return false;

        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        if(dueDate != null && dueDate.before(currentDate)) {
            // 逾期归还
            Jdatabase.updateBorrowStatus(borrowid, "逾期归还");
        } else {
            // 正常归还
            Jdatabase.updateBorrowStatus(borrowid, "归还");
        }
        // 更新归还日期
        Jdatabase.updateBorrowReturnDate(borrowid, currentDate);
        // 更新图书状态为可借
        Jdatabase.updateBookStatus(id, false);
        return true;
    }
}
