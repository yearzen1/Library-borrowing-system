package main;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import java.sql.Date;
public class User {

    User(int id,String name,String pwd)
    {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
    }
    int id;
    String name; 
    String pwd;
    Boolean register() throws Exception
    {
        if(id > 99999999 || name.length() > 50 || pwd.length() > 100)
            return false;
        ResultSet temp = Jdatabase.selectUserById(id);
        if(!temp.next())
        {
            Jdatabase.insertUser(id, name, pwd);;
            return true;
        }
        else
            return false;
    }
    Boolean login() throws Exception
    {
        ResultSet temp = Jdatabase.selectUserLogin(id, name);
        if(temp.next())
            return true;
        else
            return false;
    }
    Boolean changeName(String name) throws Exception
    {   
        if(name.length() > 50)
            return false;
        Jdatabase.updateUser(id, name, pwd);
        return true;
    }
    Boolean changePwd(String pwd) throws Exception
    {
        if(pwd.length() > 100)
            return false;
        Jdatabase.updateUser(id, name, pwd);
        return true;
    }
    ArrayList<String[]> viewAllBooks() throws Exception
    {
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
    ArrayList<String[]> viewSpecificBook(String name) throws Exception
    {
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
    ArrayList<String[]> viewAviliableBooks() throws Exception
    {
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
    private int generateBorrowId() 
    {
        return (int)(System.currentTimeMillis() % 100000000); // 简单的ID生成策略
    }
    Boolean borrowBooks(int id) throws Exception
    {
        ResultSet temp =Jdatabase.selectBookById(id);
        if(!temp.next())
            return false;
        Boolean isborrow = temp.getBoolean("isBorrow");
        if(isborrow == true)
            return false;
        Jdatabase.updateBookStatus(id, true);
        int borrowId = generateBorrowId();
        Date borrowDate = new java.sql.Date(System.currentTimeMillis());
        Date dueDate = new java.sql.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30天后到期
        Jdatabase.insertBorrow(borrowId, this.id, id, borrowDate, dueDate, null, "未归还");
        return true;

    }
    // 自动检查并更新逾期借阅记录
    private void autoUpdateOverdueBorrows() throws Exception {
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
    Boolean returnBooks(int id) throws Exception
    {
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
