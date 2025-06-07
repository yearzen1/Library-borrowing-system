import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;

public class AdminMainFrame extends JPanel {
    private Admin admin;

    public AdminMainFrame(Admin admin) {
        this.admin = admin;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // 顶部欢迎栏
        JLabel welcomeLabel = new JLabel("欢迎您，管理员：" + admin.getName());
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(30, 60, 120));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setOpaque(true);
        welcomeLabel.setBackground(new Color(220, 235, 250));
        welcomeLabel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(100, 150, 200)),
                new EmptyBorder(10, 0, 10, 0)
        ));

        add(welcomeLabel, BorderLayout.NORTH);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "功能导航",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 16),
                new Color(100, 100, 100)
        ));

        JButton manageUsersBtn    = createButton("用户管理");
        JButton manageBooksBtn    = createButton("图书管理");
        JButton manageBorrowsBtn  = createButton("借阅记录管理");
        JButton statsAnalysisBtn  = createButton("统计分析");
        JButton systemMaintainBtn = createButton("系统维护");
        JButton changePwdBtn      = createButton("修改密码");
        JButton changeNameBtn     = createButton("修改昵称");
        JButton logoutBtn         = createButton("退出登录");
        JButton addBookBtn        = createButton("添加图书");

        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(manageBooksBtn);
        buttonPanel.add(manageBorrowsBtn);
        buttonPanel.add(statsAnalysisBtn);
        buttonPanel.add(systemMaintainBtn);
        buttonPanel.add(changePwdBtn);
        buttonPanel.add(changeNameBtn);
        buttonPanel.add(logoutBtn);
        buttonPanel.add(addBookBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // 事件绑定
        manageUsersBtn.addActionListener(e -> manageUsers());
        manageBooksBtn.addActionListener(e -> manageBooks());
        manageBorrowsBtn.addActionListener(e -> manageBorrows());
        statsAnalysisBtn.addActionListener(e -> statsAnalysis());
        systemMaintainBtn.addActionListener(e -> systemMaintain());
        changePwdBtn.addActionListener(e -> changePassword());
        changeNameBtn.addActionListener(e -> changeName());
        logoutBtn.addActionListener(e -> logout());      // 退出登录：关闭当前界面并重回登录窗口
        addBookBtn.addActionListener(e -> addBook());
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btn.setBackground(new Color(240, 248, 255));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        return btn;
    }

    private void manageUsers() {
        try {
            // 创建主面板
            JPanel userPanel = new JPanel(new BorderLayout(10, 10));

            // 1. 顶部操作栏
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton refreshBtn    = createButton("刷新");
            JButton addUserBtn    = createButton("添加用户");
            JButton deleteUserBtn = createButton("删除用户");
            JButton resetPwdBtn   = createButton("重置密码");

            // 搜索功能
            JTextField searchField = new JTextField(15);
            JButton searchBtn = createButton("搜索");

            topPanel.add(refreshBtn);
            topPanel.add(addUserBtn);
            topPanel.add(deleteUserBtn);
            topPanel.add(resetPwdBtn);
            topPanel.add(new JLabel("用户ID/姓名:"));
            topPanel.add(searchField);
            topPanel.add(searchBtn);

            // 2. 用户表格
            ArrayList<String[]> userData = admin.viewAllUsers();
            String[] userColumns = {"用户ID", "用户名", "密码"}; // 根据实际数据库字段调整
            DefaultTableModel userModel = new DefaultTableModel(userColumns, 0);

            for (String[] row : userData) {
                userModel.addRow(row);
            }

            JTable userTable = new JTable(userModel);
            userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(userTable);

            // 3. 底部状态栏
            JLabel statusLabel = new JLabel("共 " + userData.size() + " 位用户");

            userPanel.add(topPanel,  BorderLayout.NORTH);
            userPanel.add(scrollPane,  BorderLayout.CENTER);
            userPanel.add(statusLabel,  BorderLayout.SOUTH);

            // 4. 按钮事件
            refreshBtn.addActionListener(e  -> {
                try {
                    refreshUserTable(userModel);
                    statusLabel.setText(" 共 " + admin.viewAllUsers().size()  + " 位用户");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            deleteUserBtn.addActionListener(e  -> {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this,  "请先选择要删除的用户");
                    return;
                }

                int userId = Integer.parseInt(userModel.getValueAt(selectedRow,  0).toString());
                try {
                    if (admin.deleteUser(userId))  {
                        refreshUserTable(userModel);
                        JOptionPane.showMessageDialog(this,  "用户删除成功");
                    } else {
                        JOptionPane.showMessageDialog(this,  "该用户有未归还图书，不能删除");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            resetPwdBtn.addActionListener(e  -> {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this,  "请先选择要重置密码的用户");
                    return;
                }

                int userId = Integer.parseInt(userModel.getValueAt(selectedRow,  0).toString());
                String newPwd = JOptionPane.showInputDialog(this,  "请输入新密码：");
                if (newPwd == null || newPwd.trim().isEmpty())  return;

                try {
                    if (admin.resetUserPassword(userId,  newPwd)) {
                        JOptionPane.showMessageDialog(this,  "密码重置成功");
                    } else {
                        JOptionPane.showMessageDialog(this,  "用户不存在");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            searchBtn.addActionListener(e  -> {
                String keyword = searchField.getText().trim();
                if (keyword.isEmpty())  {
                    try {
                        refreshUserTable(userModel);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                try {
                    // 尝试按ID搜索
                    try {
                        int userId = Integer.parseInt(keyword);
                        String[] user = admin.viewSpecificUser(userId);
                        if (user != null) {
                            userModel.setRowCount(0);
                            userModel.addRow(user);
                            statusLabel.setText(" 找到1位用户");
                            return;
                        }
                    } catch (NumberFormatException ignored) {}

                    // 按姓名模糊搜索
                    ArrayList<String[]> allUsers = admin.viewAllUsers();
                    userModel.setRowCount(0);
                    int count = 0;
                    for (String[] user : allUsers) {
                        if (user[1].contains(keyword)) { // 假设用户名在第二列
                            userModel.addRow(user);
                            count++;
                        }
                    }
                    statusLabel.setText(" 找到" + count + "位用户");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // 显示对话框
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),  "用户管理", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.getContentPane().add(userPanel);
            dialog.setSize(800,  500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,  "加载用户数据失败: " + e.getMessage(),  "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshUserTable(DefaultTableModel model) throws Exception {
        model.setRowCount(0);
        ArrayList<String[]> users = admin.viewAllUsers();
        for (String[] user : users) {
            model.addRow(user);
        }
    }

    // 系统统计分析
    private void statsAnalysis() {
        try {
            int totalBooks   = admin.getTotalBooksCount();
            int totalUsers   = admin.getTotalUsersCount();
            int borrowedBooks= admin.getBorrowedBooksCount();
            int overdueBooks = admin.getOverdueBooksCount();

            JPanel statsPanel = new JPanel();
            statsPanel.setLayout(new GridLayout(4, 1, 10, 10));
            statsPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

            JLabel totalBooksLabel   = new JLabel("📚 图书总数: " + totalBooks);
            JLabel totalUsersLabel   = new JLabel("👤 用户总数: " + totalUsers);
            JLabel borrowedBooksLabel= new JLabel("📖 当前借出图书数: " + borrowedBooks);
            JLabel overdueBooksLabel = new JLabel("⏰ 逾期图书数: " + overdueBooks);

            Font  labelFont  = new Font("微软雅黑", Font.PLAIN, 18);
            Color labelColor = new Color(30, 60, 120);

            for (JLabel label : new JLabel[]{totalBooksLabel, totalUsersLabel, borrowedBooksLabel, overdueBooksLabel}) {
                label.setFont(labelFont);
                label.setForeground(labelColor);
                statsPanel.add(label);
            }

            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "统计分析", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.getContentPane().add(statsPanel);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "统计分析失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 图书管理
    private void manageBooks() {
        try {
            // 图书数据模型
            ArrayList<String[]> books = admin.viewAllBooks();
            String[] columnNames = {"ID", "ISBN", "书名", "作者", "是否借出"};

            DefaultTableModel bookModel = new DefaultTableModel(columnNames, 0);
            for (String[] book : books) {
                bookModel.addRow(book);
            }

            JTable bookTable = new JTable(bookModel);
            bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(bookTable);

            // 顶部工具栏
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton refreshBtn   = createButton("刷新");
            JButton editBookBtn  = createButton("修改图书");
            JButton deleteBookBtn= createButton("删除图书");

            topPanel.add(refreshBtn);
            topPanel.add(editBookBtn);
            topPanel.add(deleteBookBtn);

            // 主面板组合
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            JLabel statusLabel = new JLabel("共 " + bookModel.getRowCount() + " 本图书");
            mainPanel.add(statusLabel, BorderLayout.SOUTH);

            // 事件绑定
            refreshBtn.addActionListener(e -> {
                try {
                    bookModel.setRowCount(0);
                    ArrayList<String[]> updatedBooks = admin.viewAllBooks();
                    for (String[] b : updatedBooks) {
                        bookModel.addRow(b);
                    }
                    statusLabel.setText("共 " + bookModel.getRowCount() + " 本图书");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "刷新失败：" + ex.getMessage());
                }
            });

            editBookBtn.addActionListener(e -> {
                int row = bookTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "请先选择要修改的图书！");
                    return;
                }

                try {
                    int bookId    = Integer.parseInt(bookModel.getValueAt(row, 0).toString());
                    String oldIsbn   = bookModel.getValueAt(row, 1).toString();
                    String oldTitle  = bookModel.getValueAt(row, 2).toString();
                    String oldAuthor = bookModel.getValueAt(row, 3).toString();
                    boolean isBorrowed = bookModel.getValueAt(row, 4).toString().equals("true");

                    JTextField isbnField   = new JTextField(oldIsbn);
                    JTextField titleField  = new JTextField(oldTitle);
                    JTextField authorField = new JTextField(oldAuthor);

                    JPanel editPanel = new JPanel(new GridLayout(3, 2, 10, 10));
                    editPanel.add(new JLabel("ISBN:"));
                    editPanel.add(isbnField);
                    editPanel.add(new JLabel("书名:"));
                    editPanel.add(titleField);
                    editPanel.add(new JLabel("作者:"));
                    editPanel.add(authorField);

                    int result = JOptionPane.showConfirmDialog(this, editPanel, "修改图书信息", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        boolean success = admin.updateBookInfo(
                                bookId,
                                isbnField.getText(),
                                titleField.getText(),
                                authorField.getText(),
                                isBorrowed
                        );

                        if (success) {
                            JOptionPane.showMessageDialog(this, "图书信息更新成功！");
                            refreshBtn.doClick();  // 自动刷新表格
                        } else {
                            JOptionPane.showMessageDialog(this, "图书不存在或更新失败！");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "操作失败：" + ex.getMessage());
                }
            });

            deleteBookBtn.addActionListener(e -> {
                int row = bookTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "请先选择要删除的图书！");
                    return;
                }

                int bookId = Integer.parseInt(bookModel.getValueAt(row, 0).toString());
                try {
                    boolean success = admin.deleteBook(bookId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "图书删除成功！");
                        refreshBtn.doClick();
                    } else {
                        JOptionPane.showMessageDialog(this, "图书已被借出，不能删除！");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "删除失败：" + ex.getMessage());
                }
            });

            // 显示对话框
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "图书管理", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.getContentPane().add(mainPanel);
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载图书失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageBorrows() {
        try {
            JPanel borrowPanel = new JPanel(new BorderLayout(10, 10));

            // 1. 顶部操作栏
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton refreshBtn     = createButton("刷新");
            JButton viewOverdueBtn = createButton("查看逾期");
            JButton deleteRecordBtn= createButton("删除记录");

            JTextField searchField = new JTextField(15);
            JButton searchBtn      = createButton("搜索");

            topPanel.add(refreshBtn);
            topPanel.add(viewOverdueBtn);
            topPanel.add(deleteRecordBtn);
            topPanel.add(new JLabel("用户ID:"));
            topPanel.add(searchField);
            topPanel.add(searchBtn);

            // 2. 借阅记录表格
            ArrayList<String[]> borrowData = admin.viewAllBorrows();
            String[] borrowColumns = {
                "借阅ID", "用户ID", "图书ID",
                "借出日期", "应还日期", "状态",
                "归还日期", "图书状态"
            };
            DefaultTableModel borrowModel = new DefaultTableModel(borrowColumns, 0);

            if (borrowData != null) {
                for (String[] row : borrowData) {
                    borrowModel.addRow(row);
                }
            }

            JTable borrowTable = new JTable(borrowModel);
            borrowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(borrowTable);

            // 3. 底部状态栏
            JLabel statusLabel = new JLabel("共 " + (borrowData == null ? 0 : borrowData.size()) + " 条借阅记录");

            borrowPanel.add(topPanel, BorderLayout.NORTH);
            borrowPanel.add(scrollPane, BorderLayout.CENTER);
            borrowPanel.add(statusLabel, BorderLayout.SOUTH);

            // 4. 事件处理逻辑
            refreshBtn.addActionListener(e -> {
                try {
                    refreshBorrowTable(borrowModel);
                    ArrayList<String[]> updated = admin.viewAllBorrows();
                    statusLabel.setText(" 共 " + (updated == null ? 0 : updated.size()) + " 条借阅记录");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            viewOverdueBtn.addActionListener(e -> {
                try {
                    ArrayList<String[]> overdueData = admin.viewOverdueBorrows();
                    borrowModel.setRowCount(0);
                    if (overdueData != null) {
                        for (String[] row : overdueData) {
                            borrowModel.addRow(row);
                        }
                        statusLabel.setText(" 共 " + overdueData.size() + " 条逾期记录");
                    } else {
                        statusLabel.setText("暂无逾期记录");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            deleteRecordBtn.addActionListener(e -> {
                int selectedRow = borrowTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "请先选择要删除的记录");
                    return;
                }

                int borrowId = Integer.parseInt(borrowModel.getValueAt(selectedRow, 0).toString());
                try {
                    if (admin.deleteBorrowRecord(borrowId)) {
                        refreshBorrowTable(borrowModel);
                        JOptionPane.showMessageDialog(this, "记录删除成功");
                    } else {
                        JOptionPane.showMessageDialog(this, "只能删除已归还的记录");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            searchBtn.addActionListener(e -> {
                String keyword = searchField.getText().trim();
                if (keyword.isEmpty()) {
                    try {
                        refreshBorrowTable(borrowModel);
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                try {
                    int userId = Integer.parseInt(keyword);
                    ArrayList<String[]> userBorrows = admin.viewUserBorrows(userId);
                    borrowModel.setRowCount(0);
                    if (userBorrows != null) {
                        for (String[] row : userBorrows) {
                            borrowModel.addRow(row);
                        }
                        statusLabel.setText(" 用户 " + userId + " 的 " + userBorrows.size() + " 条借阅记录");
                    } else {
                        statusLabel.setText(" 用户 " + userId + " 无借阅记录");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "请输入有效的用户ID");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // 显示对话框
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "借阅记录管理", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.getContentPane().add(borrowPanel);
            dialog.setSize(1000, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载借阅数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBorrowTable(DefaultTableModel borrowModel) {
        try {
            borrowModel.setRowCount(0);
            ArrayList<String[]> allBorrows = admin.viewAllBorrows();
            if (allBorrows != null) {
                for (String[] row : allBorrows) {
                    borrowModel.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changePassword() {
        String pwd = JOptionPane.showInputDialog(this, "请输入新密码：");
        if (pwd == null || pwd.trim().isEmpty()) return;
        try {
            admin.changePwd(pwd);
            JOptionPane.showMessageDialog(this, "密码修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "密码修改失败！");
        }
    }

    private void changeName() {
        String name = JOptionPane.showInputDialog(this, "请输入新昵称：");
        if (name == null || name.trim().isEmpty()) return;
        try {
            admin.changeName(name);
            JOptionPane.showMessageDialog(this, "昵称修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "昵称修改失败！");
        }
    }

    /**
     * 退出登录：先关闭当前管理员窗口，然后重新打开登录界面
     */
    private void logout() {
        // 获取当前所在的顶层窗口并关闭
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        // 异步回到登录窗口
        SwingUtilities.invokeLater(() -> {
            // 假设 LoginWindow 构造需要一个 User 实例以及一个 Admin 实例，
            // 这里可以传入空的对象或根据实际构造参数调整：
            LoginWindow loginWindow = new LoginWindow(new User(), new Admin());
            loginWindow.setVisible(true);
        });
    }

    // 系统维护功能
    private void systemMaintain() {
        try {
            admin.updateAllOverdueStatus(); // 调用管理员方法执行维护逻辑
            JOptionPane.showMessageDialog(this, "系统维护完成：已更新所有逾期状态", "系统维护", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "系统维护失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 添加图书功能实现
    private void addBook() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("图书名称："));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("作者："));
        JTextField authorField = new JTextField();
        inputPanel.add(authorField);

        inputPanel.add(new JLabel("ISBN："));
        JTextField isbnField = new JTextField();
        inputPanel.add(isbnField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "添加新图书",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title  = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn   = isbnField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "所有字段均不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                admin.addBook(isbn, title, author);
                JOptionPane.showMessageDialog(this, "图书添加成功！");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "库存数量必须为正整数！", "输入错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加图书失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
