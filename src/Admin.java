import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.Date;

public class Admin {
    private int id;
    private String name;
    private String pwd;

    // 构造方法
    public Admin(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public Admin() {
        //TODO Auto-generated constructor stub
    }

    // =================== 管理员基本操作 ===================
    // 设置管理员姓名
    public void setName(String name) {
        this.name = name;
    }
    // 设置管理员密码
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    // 获取管理员ID
    public int getId() {
        return id;
    }
    // 获取管理员姓名
    public String getName() {
        return name;
    }
    // 获取管理员密码
    public String getPwd() {
        return pwd;
    }
    // 管理员登录
    Boolean login() throws Exception {
        // 检查管理员账号和密码是否匹配
        ResultSet temp = Jdatabase.selectAdminByName(name);
        if(temp.next() && pwd.equals(temp.getString(3)))
            return true;
        else
            return false;
    }

    // 修改管理员姓名
    void changeName(String name) throws Exception {
        // 修改管理员姓名，长度不能超过20
        Jdatabase.updateAdmin(id, name, pwd);
        this.name = name;
    }

    // 修改管理员密码
    void changePwd(String pwd) throws Exception {
        // 修改管理员密码，长度不能超过100
        Jdatabase.updateAdmin(id, name, pwd);
        this.pwd = pwd;
    }

    // =================== 用户管理功能 ===================

    // 查看所有用户
    ArrayList<String[]> viewAllUsers() throws Exception {
        // 返回所有用户的信息列表
        ResultSet temp = Jdatabase.selectAllUsers();
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        while(temp.next()) {
            String[] strarray = new String[tempdata.getColumnCount()];
            for(int i = 0; i <= strarray.length - 1; ++i)
                strarray[i] = temp.getString(i + 1);
            array.add(strarray);
        }
        return array;
    }

    // 查看特定用户
    String[] viewSpecificUser(int userId) throws Exception {
        // 根据用户ID返回用户信息
        ResultSet temp = Jdatabase.selectUserById(userId);
        if(!temp.next())
            return null;
        ResultSetMetaData tempdata = temp.getMetaData();
        String[] strarray = new String[tempdata.getColumnCount()];
        for(int i = 0; i <= strarray.length - 1; ++i)
            strarray[i] = temp.getString(i + 1);
        return strarray;
    }

    // 删除用户
    Boolean deleteUser(int userId) throws Exception {
        // 检查用户是否有未归还的图书，若无则删除用户
        ResultSet borrowRecords = Jdatabase.selectBorrowsByUser(userId);
        while(borrowRecords.next()) {
            String status = borrowRecords.getString("status");
            if("未归还".equals(status) || "逾期未归还".equals(status)) {
                return false; // 有未归还图书，不能删除用户
            }
        }
        Jdatabase.deleteUser(userId);
        return true;
    }

    // 重置用户密码
    Boolean resetUserPassword(int userId, String newPassword) throws Exception {
        // 重置指定用户的密码
        ResultSet temp = Jdatabase.selectUserById(userId);
        if(!temp.next())
            return false;
        String userName = temp.getString("name");
        Jdatabase.updateUser(userId, userName, newPassword);
        return true;
    }

    // =================== 图书管理功能 ===================

    // 查看所有图书
    ArrayList<String[]> viewAllBooks() throws Exception {
        // 返回所有图书的信息列表
        ResultSet temp = Jdatabase.selectAllBooks();
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        while(temp.next()) {
            String[] strarray = new String[tempdata.getColumnCount()];
            for(int i = 0; i <= strarray.length - 1; ++i)
                strarray[i] = temp.getString(i + 1);
            array.add(strarray);
        }
        return array;
    }


     public double calculateFine(int borrowId) throws Exception {
        ResultSet rs = Jdatabase.selectBorrowById(borrowId);
        if (!rs.next()) {
            return 0;
        }
        String status = rs.getString("status");
        // 只有“逾期未归还”状态才罚款
        if (!"逾期未归还".equals(status)) {
            return 0;
        }
        Date dueDate = rs.getDate("duedate");
        long overdueMs = System.currentTimeMillis() - dueDate.getTime();
        long days = overdueMs / (1000L * 60 * 60 * 24);
        return days > 0 ? days * 1.0 : 0;
    }

    /**
     * 对用户执行罚款：在数据库中新增一条罚款记录，并更新用户累计欠款
     * @param userId 用户 ID
     * @param amount 本次罚款金额
     * @return true 若操作成功
     */
    public boolean imposeFine(int userId, double amount) throws Exception {
        if (amount <= 0) {
            return false;
        }
        // 假定 Jdatabase.insertFine 会：
        // 1) 在 fines 表中插入一条 (userId, amount, now)
        // 2) 同步更新 users 表中的 total_fine 字段
        return Jdatabase.insertFine(userId, amount);
    }

    /**
     * 获取用户当前的累计欠款
     * @param userId 用户 ID
     * @return 数据库中 users.total_fine 字段值，若不存在则返回 0
     */
    public double getUserTotalFine(int userId) throws Exception {
        ResultSet rs = Jdatabase.selectUserById(userId);
        if (rs.next()) {
            // 假定 users 表里多了一个 total_fine 列
            return rs.getDouble("total_fine");
        }
        return 0;
    }

    // 添加图书
    Boolean addBook(String isbn, String title, String author) throws Exception {
        // 添加新图书，若name已存在则返回false
        ResultSet temp = Jdatabase.selectBooksByTitle(name);
        if(!temp.next()) {
            Jdatabase.insertBook(isbn, title, author, false);
            return true;
        } else {
            return false; // 图书name已存在
        }
    }

    // 修改图书信息
    Boolean updateBookInfo(int bookId, String isbn, String title, String author,boolean isborrow) throws Exception {
        // 修改指定图书的信息
        ResultSet temp = Jdatabase.selectBookById(bookId);
        if(!temp.next())
            return false;
        Jdatabase.updateBook(bookId, isbn, title, author,isborrow);
        return true;
    }

    // 删除图书
    Boolean deleteBook(int bookId) throws Exception {
        // 检查图书是否被借出，若未借出则删除
        ResultSet temp = Jdatabase.selectBookById(bookId);
        if(!temp.next())
            return false;
        boolean isBorrowed = temp.getBoolean("isBorrow");
        if(isBorrowed) {
            return false; // 图书已被借出，不能删除
        }
        Jdatabase.deleteBook(bookId);
        return true;
    }
    // =================== 借阅记录管理 ===================

    // 查看所有借阅记录
    ArrayList<String[]> viewAllBorrows() throws Exception {
        // 返回所有借阅记录
        ResultSet temp = Jdatabase.selectAllBorrows();
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        while(temp.next()) {
            String[] strarray = new String[tempdata.getColumnCount()];
            for(int i = 0; i <= strarray.length - 1; ++i)
                strarray[i] = temp.getString(i + 1);
            array.add(strarray);
        }
        return array;
    }
public void updateBookStatus(int bookId, boolean isBorrowed) throws Exception {
    // 先查找图书，确认存在
    ResultSet rs = Jdatabase.selectBookById(bookId);
    if (!rs.next()) {
        throw new Exception("图书不存在");
    }
    String isbn = rs.getString("isbn");
    String title = rs.getString("title");
    String author = rs.getString("author");

    // 调用 updateBookInfo 更新状态，其它信息保持不变
    boolean result = updateBookInfo(bookId, isbn, title, author, isBorrowed);
    if (!result) {
        throw new Exception("更新失败");
    }
}

    // 查看特定用户的借阅记录
    ArrayList<String[]> viewUserBorrows(int userId) throws Exception {
        // 返回指定用户的所有借阅记录
        ResultSet temp = Jdatabase.selectBorrowsByUser(userId);
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        while(temp.next()) {
            String[] strarray = new String[tempdata.getColumnCount()];
            for(int i = 0; i <= strarray.length - 1; ++i)
                strarray[i] = temp.getString(i + 1);
            array.add(strarray);
        }
        return array;
    }

    // 查看所有逾期借阅记录
    ArrayList<String[]> viewOverdueBorrows() throws Exception {
        // 返回所有逾期借阅记录
        ResultSet temp = Jdatabase.selectOverdueBorrows();
        if(!temp.next())
            return null;
        temp.beforeFirst();
        ArrayList<String[]> array = new ArrayList<>();
        ResultSetMetaData tempdata = temp.getMetaData();
        while(temp.next()) {
            String[] strarray = new String[tempdata.getColumnCount()];
            for(int i = 0; i <= strarray.length - 1; ++i)
                strarray[i] = temp.getString(i + 1);
            array.add(strarray);
        }
        return array;
    }

    // 删除借阅记录（已归还的记录）
    Boolean deleteBorrowRecord(int borrowId) throws Exception {
        // 仅删除已归还或逾期归还的借阅记录
        ResultSet temp = Jdatabase.selectBorrowById(borrowId);
        if(!temp.next())
            return false;
        String status = temp.getString("status");
        if("归还".equals(status) || "逾期归还".equals(status)) {
            Jdatabase.deleteBorrow(borrowId);
            return true;
        } else {
            return false; // 未归还的记录不能删除
        }
    }
    

    // =================== 统计分析功能 ===================

    // 统计图书总数
    int getTotalBooksCount() throws Exception {
        // 返回图书总数
        ResultSet temp = Jdatabase.selectAllBooks();
        int count = 0;
        while(temp.next()) {
            count++;
        }
        return count;
    }

    // 统计用户总数
    int getTotalUsersCount() throws Exception {
        // 返回用户总数
        ResultSet temp = Jdatabase.selectAllUsers();
        int count = 0;
        while(temp.next()) {
            count++;
        }
        return count;
    }

    // 统计当前借出图书数
    int getBorrowedBooksCount() throws Exception {
        // 返回当前被借出的图书数量
        ResultSet temp = Jdatabase.selectAllBooks();
        int count = 0;
        while(temp.next()) {
            if(temp.getBoolean("isBorrow")) {
                count++;
            }
        }
        return count;
    }

    // 统计逾期图书数
    int getOverdueBooksCount() throws Exception {
        // 返回逾期图书数量
        ResultSet temp = Jdatabase.selectOverdueBorrows();
        int count = 0;
        while(temp.next()) {
            count++;
        }
        return count;
    }

    // =================== 系统维护功能 ===================

    // 全局更新逾期状态
    void updateAllOverdueStatus() throws Exception {
        // 检查所有借阅记录，若已逾期则更新状态
        ResultSet temp = Jdatabase.selectAllBorrows();
        Date currentDate = new java.sql.Date(System.currentTimeMillis());
        while(temp.next()) {
            String status = temp.getString("status");
            Date dueDate = temp.getDate("duedate");
            int borrowId = temp.getInt("borrowid");
            if("未归还".equals(status) && dueDate != null && dueDate.before(currentDate)) {
                Jdatabase.updateBorrowStatus(borrowId, "逾期未归还");
            }
        }
    }
}