package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class Jdatabase {
    public static String database = "library_borrow_system";
    public static String usertable = "User";
    public static String admintable = "Admin";
    public static String booktable = "Book";
    public static String borrowtable = "Borrow";

    private static String url = "jdbc:mysql://localhost:3306/mysql?serverTimezone=Asia/Shanghai";

    private static String createdatabase = "create database if not exists library_borrow_system";
    private static String createUsertable = "create table if not exists library_borrow_system.User(" +
                                                "id int primary key," +
                                                "name varchar(50),"  +
                                                "pwd varchar(100))" ;
    private static String createAdmintable = "create table if not exists library_borrow_system.Admin(" +
                                            "id int primary key," +
                                            "name varchar(20)," +
                                            "pwd varchar(100))";
    private static String createBooktable = "create table if not exists library_borrow_system.Book(" +
                                            "id int primary key," +
                                            "isbn varchar(20)," +
                                            "title varchar(200)," +
                                            "author varchar(100)," +
                                            "isBorrow bit(1))";
    private static String createBorrowtable = "create table if not exists library_borrow_system.Borrow(" +
                                            "borrowid int primary key," +
                                            "userid int," +
                                            "bookid int," +
                                            "borrowdate Date," +
                                            "duedate Date," +
                                            "returndate Date," +
                                            "status varchar(4)," +
                                            "check(status in ('归还','未归还','逾期归还','逾期未归还')))";

    private static PreparedStatement preparedStatement = null;
    private static Connection connection = null;

    private static String insertUser = "INSERT INTO library_borrow_system.User (id, name, pwd) VALUES (?, ?, ?)";
    private static String insertAdmin = "INSERT INTO library_borrow_system.Admin (id, name, pwd) VALUES (?, ?, ?)";
    private static String insertBook = "INSERT INTO library_borrow_system.Book (id, isbn, title, author, isBorrow) VALUES (?, ?, ?, ?, ?)";
    private static String insertBorrow = "INSERT INTO library_borrow_system.Borrow (borrowid, userid, bookid, borrowdate, duedate, returndate, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static void insertUser(int userId, String userName, String userPwd) throws Exception 
    {
        preparedStatement = connection.prepareStatement(insertUser);
        preparedStatement.setInt(1, userId);
        preparedStatement.setString(2, userName);
        preparedStatement.setString(3, userPwd);
        preparedStatement.executeUpdate();
    }
    public static void insertAdmin(int adminId, String adminName, String adminPwd) throws Exception 
    {
        preparedStatement = connection.prepareStatement(insertAdmin);
        preparedStatement.setInt(1, adminId);
        preparedStatement.setString(2, adminName);
        preparedStatement.setString(3, adminPwd);
        preparedStatement.executeUpdate();
    }
    public static void insertBook(int bookId, String isbn, String title, String author, boolean isBorrow) throws Exception 
    {
        preparedStatement = connection.prepareStatement(insertBook);
        preparedStatement.setInt(1, bookId);
        preparedStatement.setString(2, isbn);
        preparedStatement.setString(3, title);
        preparedStatement.setString(4, author);
        preparedStatement.setBoolean(5, isBorrow);
        preparedStatement.executeUpdate();
    }
    public static void insertBorrow(int borrowId, int userId, int bookId, 
                            java.sql.Date borrowDate, java.sql.Date dueDate, 
                            java.sql.Date returnDate, String status) throws Exception 
    {
        preparedStatement = connection.prepareStatement(insertBorrow);
        preparedStatement.setInt(1, borrowId);
        preparedStatement.setInt(2, userId);
        preparedStatement.setInt(3, bookId);
        preparedStatement.setDate(4, borrowDate);
        preparedStatement.setDate(5, dueDate);
        preparedStatement.setDate(6, returnDate);
        preparedStatement.setString(7, status);
        preparedStatement.executeUpdate();
    }

    private static String updateUser = "UPDATE library_borrow_system.User SET name = ?, pwd = ? WHERE id = ?";
    private static String updateAdmin = "UPDATE library_borrow_system.Admin SET name = ?, pwd = ? WHERE id = ?";
    private static String updateBook = "UPDATE library_borrow_system.Book SET isbn = ?, title = ?, author = ?, isBorrow = ? WHERE id = ?";
    private static String updateBorrow = "UPDATE library_borrow_system.Borrow SET userid = ?, bookid = ?, borrowdate = ?, duedate = ?, returndate = ?, status = ? WHERE borrowid = ?";

    public static void updateUser(int userId, String userName, String userPwd) throws Exception {
        preparedStatement = connection.prepareStatement(updateUser);
        preparedStatement.setString(1, userName);
        preparedStatement.setString(2, userPwd);
        preparedStatement.setInt(3, userId);
        preparedStatement.executeUpdate();
    }

    public static void updateAdmin(int adminId, String adminName, String adminPwd) throws Exception {
        preparedStatement = connection.prepareStatement(updateAdmin);
        preparedStatement.setString(1, adminName);
        preparedStatement.setString(2, adminPwd);
        preparedStatement.setInt(3, adminId);
        preparedStatement.executeUpdate();
    }

    public static void updateBook(int bookId, String isbn, String title, String author, boolean isBorrow) throws Exception {
        preparedStatement = connection.prepareStatement(updateBook);
        preparedStatement.setString(1, isbn);
        preparedStatement.setString(2, title);
        preparedStatement.setString(3, author);
        preparedStatement.setBoolean(4, isBorrow);
        preparedStatement.setInt(5, bookId);
        preparedStatement.executeUpdate();
    }

    public static void updateBorrow(int borrowId, int userId, int bookId, 
                            java.sql.Date borrowDate, java.sql.Date dueDate, 
                            java.sql.Date returnDate, String status) throws Exception {
        preparedStatement = connection.prepareStatement(updateBorrow);
        preparedStatement.setInt(1, userId);
        preparedStatement.setInt(2, bookId);
        preparedStatement.setDate(3, borrowDate);
        preparedStatement.setDate(4, dueDate);
        preparedStatement.setDate(5, returnDate);
        preparedStatement.setString(6, status);
        preparedStatement.setInt(7, borrowId);
        preparedStatement.executeUpdate();
    }

    public static void updateBookStatus(int bookId, boolean isBorrow) throws Exception {
        String updateBookStatus = "UPDATE library_borrow_system.Book SET isBorrow = ? WHERE id = ?";
        preparedStatement = connection.prepareStatement(updateBookStatus);
        preparedStatement.setBoolean(1, isBorrow);
        preparedStatement.setInt(2, bookId);
        preparedStatement.executeUpdate();
    }

    public static void updateBorrowStatus(int borrowId, String status) throws Exception {
        String updateBorrowStatus = "UPDATE library_borrow_system.Borrow SET status = ? WHERE borrowid = ?";
        preparedStatement = connection.prepareStatement(updateBorrowStatus);
        preparedStatement.setString(1, status);
        preparedStatement.setInt(2, borrowId);
        preparedStatement.executeUpdate();
    }

    public static void updateBorrowReturnDate(int borrowId, java.sql.Date returnDate) throws Exception {
        String sql = "UPDATE library_borrow_system.Borrow SET returndate = ? WHERE borrowid = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1, returnDate);
        preparedStatement.setInt(2, borrowId);
        preparedStatement.executeUpdate();
    }

    private static String deleteUser = "DELETE FROM library_borrow_system.User WHERE id = ?";
    private static String deleteAdmin = "DELETE FROM library_borrow_system.Admin WHERE id = ?";
    private static String deleteBook = "DELETE FROM library_borrow_system.Book WHERE id = ?";
    private static String deleteBorrow = "DELETE FROM library_borrow_system.Borrow WHERE borrowid = ?";

    public static void deleteUser(int userId) throws Exception {
        preparedStatement = connection.prepareStatement(deleteUser);
        preparedStatement.setInt(1, userId);
        preparedStatement.executeUpdate();
    }

    public static void deleteAdmin(int adminId) throws Exception {
        preparedStatement = connection.prepareStatement(deleteAdmin);
        preparedStatement.setInt(1, adminId);
        preparedStatement.executeUpdate();
    }

    public static void deleteBook(int bookId) throws Exception {
        preparedStatement = connection.prepareStatement(deleteBook);
        preparedStatement.setInt(1, bookId);
        preparedStatement.executeUpdate();
    }

    public static void deleteBorrow(int borrowId) throws Exception {
        preparedStatement = connection.prepareStatement(deleteBorrow);
        preparedStatement.setInt(1, borrowId);
        preparedStatement.executeUpdate();
    }

    // SELECT查询的SQL语句
    private static String selectAllUsers = "SELECT * FROM library_borrow_system.User";
    private static String selectUserById = "SELECT * FROM library_borrow_system.User WHERE id = ?";
    private static String selectAllAdmins = "SELECT * FROM library_borrow_system.Admin";
    private static String selectAdminById = "SELECT * FROM library_borrow_system.Admin WHERE id = ?";
    private static String selectAllBooks = "SELECT * FROM library_borrow_system.Book";
    private static String selectBookById = "SELECT * FROM library_borrow_system.Book WHERE id = ?";
    private static String selectAllBorrows = "SELECT * FROM library_borrow_system.Borrow";
    private static String selectBorrowById = "SELECT * FROM library_borrow_system.Borrow WHERE borrowid = ?";

    // 查询所有用户
    public static ResultSet selectAllUsers() throws Exception {
        preparedStatement = connection.prepareStatement(selectAllUsers);
        return preparedStatement.executeQuery();
    }

    // 根据ID查询用户
    public static ResultSet selectUserById(int userId) throws Exception {
        preparedStatement = connection.prepareStatement(selectUserById);
        preparedStatement.setInt(1, userId);
        return preparedStatement.executeQuery();
    }

    // 查询所有管理员
    public static ResultSet selectAllAdmins() throws Exception {
        preparedStatement = connection.prepareStatement(selectAllAdmins);
        return preparedStatement.executeQuery();
    }

    // 根据ID查询管理员
    public static ResultSet selectAdminById(int adminId) throws Exception {
        preparedStatement = connection.prepareStatement(selectAdminById);
        preparedStatement.setInt(1, adminId);
        return preparedStatement.executeQuery();
    }

    // 查询所有图书
    public static ResultSet selectAllBooks() throws Exception {
        preparedStatement = connection.prepareStatement(selectAllBooks);
        return preparedStatement.executeQuery();
    }

    // 根据ID查询图书
    public static ResultSet selectBookById(int bookId) throws Exception {
        preparedStatement = connection.prepareStatement(selectBookById);
        preparedStatement.setInt(1, bookId);
        return preparedStatement.executeQuery();
    }

    // 查询所有借阅记录
    public static ResultSet selectAllBorrows() throws Exception {
        preparedStatement = connection.prepareStatement(selectAllBorrows);
        return preparedStatement.executeQuery();
    }

    // 根据ID查询借阅记录
    public static ResultSet selectBorrowById(int borrowId) throws Exception {
        preparedStatement = connection.prepareStatement(selectBorrowById);
        preparedStatement.setInt(1, borrowId);
        return preparedStatement.executeQuery();
    }

    // 根据用户ID查询借阅记录
    public static ResultSet selectBorrowsByUser(int userId) throws Exception {
        String selectBorrowsByUser = "SELECT * FROM library_borrow_system.Borrow WHERE userid = ?";
        preparedStatement = connection.prepareStatement(selectBorrowsByUser);
        preparedStatement.setInt(1, userId);
        return preparedStatement.executeQuery();
    }

    // 根据图书ID查询借阅记录
    public static ResultSet selectBorrowsByBook(int bookId) throws Exception {
        String selectBorrowsByBook = "SELECT * FROM library_borrow_system.Borrow WHERE bookid = ?";
        preparedStatement = connection.prepareStatement(selectBorrowsByBook);
        preparedStatement.setInt(1, bookId);
        return preparedStatement.executeQuery();
    }

    // 查询可借图书
    public static ResultSet selectAvailableBooks() throws Exception {
        String selectAvailableBooks = "SELECT * FROM library_borrow_system.Book WHERE isBorrow = 0";
        preparedStatement = connection.prepareStatement(selectAvailableBooks);
        return preparedStatement.executeQuery();
    }

    // 根据书名模糊查询图书
    public static ResultSet selectBooksByTitle(String title) throws Exception {
        String selectBooksByTitle = "SELECT * FROM library_borrow_system.Book WHERE title LIKE ?";
        preparedStatement = connection.prepareStatement(selectBooksByTitle);
        preparedStatement.setString(1, "%" + title + "%");
        return preparedStatement.executeQuery();
    }

    // 根据作者查询图书
    public static ResultSet selectBooksByAuthor(String author) throws Exception {
        String selectBooksByAuthor = "SELECT * FROM library_borrow_system.Book WHERE author = ?";
        preparedStatement = connection.prepareStatement(selectBooksByAuthor);
        preparedStatement.setString(1, author);
        return preparedStatement.executeQuery();
    }

    // 查询逾期的借阅记录
    public static ResultSet selectOverdueBorrows() throws Exception {
        String selectOverdueBorrows = "SELECT * FROM library_borrow_system.Borrow WHERE duedate < CURDATE() AND returndate IS NULL";
        preparedStatement = connection.prepareStatement(selectOverdueBorrows);
        return preparedStatement.executeQuery();
    }

    // 用户登录验证
    public static ResultSet selectUserLogin(int userId, String password) throws Exception {
        String selectUserLogin = "SELECT * FROM library_borrow_system.User WHERE id = ? AND pwd = ?";
        preparedStatement = connection.prepareStatement(selectUserLogin);
        preparedStatement.setInt(1, userId);
        preparedStatement.setString(2, password);
        return preparedStatement.executeQuery();
    }

    // 管理员登录验证
    public static ResultSet selectAdminLogin(int adminId, String password) throws Exception {
        String selectAdminLogin = "SELECT * FROM library_borrow_system.Admin WHERE id = ? AND pwd = ?";
        preparedStatement = connection.prepareStatement(selectAdminLogin);
        preparedStatement.setInt(1, adminId);
        preparedStatement.setString(2, password);
        return preparedStatement.executeQuery();
    }

    static void init(String user,String pwd) throws Exception //数据库账号 密码
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        if((connection = DriverManager.getConnection(url, user, pwd)) == null)
            System.out.println("error");
        else
            System.out.println("success");
        
        //建数据库和表
        preparedStatement = connection.prepareStatement(createdatabase);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createUsertable);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createAdmintable);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createBooktable);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createBorrowtable);
        preparedStatement.executeUpdate();
        //初始化表
    }
}
