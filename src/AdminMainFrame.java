import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;

public class AdminMainFrame extends JPanel {
    private Admin admin;

    public AdminMainFrame(Admin admin) {
        this.admin = admin;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 顶部渐变背景
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int width = getWidth(), height = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(200, 225, 255), 0, height, new Color(240, 250, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, width, height);
            }
        };
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("欢迎您，管理员：" + admin.getName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(30, 60, 120));
        header.add(welcomeLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // 功能卡片区
        String[] labels = {"用户管理", "图书管理", "借阅记录管理", "统计分析", "系统维护", "修改密码", "修改昵称", "罚款管理", "添加图书"};
        JPanel grid = new JPanel(new GridLayout(3, 3, 15, 15));
        grid.setOpaque(false);
        for (String text : labels) {
            JButton btn = createCardButton(text);
            grid.add(btn);
        }
        add(grid, BorderLayout.CENTER);

        // 底部退出登录按钮
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        JButton logoutBtn = createButton("退出登录");
        logoutBtn.setBackground(new Color(200, 60, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(100, 36));
        logoutBtn.setBorder(new LineBorder(new Color(170, 30, 30), 1, true));
        logoutBtn.addActionListener(e -> logout());
        south.add(logoutBtn);
        add(south, BorderLayout.SOUTH);
    }

    private JButton createCardButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(30, 60, 120));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(20, 10, 20, 10));
        btn.setUI(new StyledButtonUI());
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(220, 235, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        btn.addActionListener(e -> handleAction(btn.getText()));
        return btn;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        btn.setBackground(new Color(30, 60, 120));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(20, 40, 100), 1, true));
        return btn;
    }

    private void handleAction(String action) {
        switch (action) {
            case "用户管理":
                manageUsers();
                break;
            case "图书管理":
                manageBooks();
                break;
            case "借阅记录管理":
                manageBorrows();
                break;
            case "统计分析":
                statsAnalysis();
                break;
            case "系统维护":
                systemMaintain();
                break;
            case "修改密码":
                changePassword();
                break;
            case "修改昵称":
                changeName();
                break;
            case "罚款管理":
                manageFines();
                break;
            case "添加图书":
                addBook();
                break;
        }
    }

    private void manageUsers() {
        SwingUtilities.invokeLater(() -> {
            try {
                JPanel userPanel = new JPanel(new BorderLayout(10, 10));
                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton refreshBtn = createButton("刷新");
                JButton addUserBtn = createButton("添加用户");
                JButton deleteUserBtn = createButton("删除用户");
                JButton resetPwdBtn = createButton("重置密码");
                JTextField searchField = new JTextField(15);
                JButton searchBtn = createButton("搜索");

                topPanel.add(refreshBtn);
                topPanel.add(addUserBtn);
                topPanel.add(deleteUserBtn);
                topPanel.add(resetPwdBtn);
                topPanel.add(new JLabel("用户ID/姓名:"));
                topPanel.add(searchField);
                topPanel.add(searchBtn);

                ArrayList<String[]> userData = admin.viewAllUsers();
                String[] userColumns = {"用户ID", "用户名", "密码"};
                DefaultTableModel userModel = new DefaultTableModel(userColumns, 0);
                for (String[] row : userData) {
                    userModel.addRow(row);
                }
                JTable userTable = new JTable(userModel);
                userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(userTable);
                JLabel statusLabel = new JLabel("共 " + userData.size() + " 位用户");

                userPanel.add(topPanel, BorderLayout.NORTH);
                userPanel.add(scrollPane, BorderLayout.CENTER);
                userPanel.add(statusLabel, BorderLayout.SOUTH);

                refreshBtn.addActionListener(e -> {
                    try {
                        refreshUserTable(userModel);
                        statusLabel.setText("共 " + admin.viewAllUsers().size() + " 位用户");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "刷新失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                deleteUserBtn.addActionListener(e -> {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(this, "请先选择要删除的用户");
                        return;
                    }
                    int userId = Integer.parseInt(userModel.getValueAt(selectedRow, 0).toString());
                    try {
                        if (admin.deleteUser(userId)) {
                            refreshUserTable(userModel);
                            statusLabel.setText("共 " + admin.viewAllUsers().size() + " 位用户");
                            JOptionPane.showMessageDialog(this, "用户删除成功");
                        } else {
                            JOptionPane.showMessageDialog(this, "该用户有未归还图书，不能删除");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                resetPwdBtn.addActionListener(e -> {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(this, "请先选择要重置密码的用户");
                        return;
                    }
                    int userId = Integer.parseInt(userModel.getValueAt(selectedRow, 0).toString());
                    String newPwd = JOptionPane.showInputDialog(this, "请输入新密码：");
                    if (newPwd == null || newPwd.trim().isEmpty()) return;
                    try {
                        if (admin.resetUserPassword(userId, newPwd)) {
                            JOptionPane.showMessageDialog(this, "密码重置成功");
                        } else {
                            JOptionPane.showMessageDialog(this, "用户不存在");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "密码重置失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                searchBtn.addActionListener(e -> {
                    String keyword = searchField.getText().trim();
                    if (keyword.isEmpty()) {
                        try {
                            refreshUserTable(userModel);
                            statusLabel.setText("共 " + admin.viewAllUsers().size() + " 位用户");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "刷新失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                        return;
                    }
                    try {
                        userModel.setRowCount(0);
                        int count = 0;
                        try {
                            int userId = Integer.parseInt(keyword);
                            String[] user = admin.viewSpecificUser(userId);
                            if (user != null) {
                                userModel.addRow(user);
                                count = 1;
                            }
                        } catch (NumberFormatException ignored) {
                            ArrayList<String[]> allUsers = admin.viewAllUsers();
                            for (String[] user : allUsers) {
                                if (user[1].contains(keyword)) {
                                    userModel.addRow(user);
                                    count++;
                                }
                            }
                        }
                        statusLabel.setText("找到 " + count + " 位用户");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "搜索失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "用户管理", Dialog.ModalityType.APPLICATION_MODAL);
                dialog.getContentPane().add(userPanel);
                dialog.setSize(800, 500);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载用户数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void refreshUserTable(DefaultTableModel model) throws Exception {
        model.setRowCount(0);
        ArrayList<String[]> users = admin.viewAllUsers();
        for (String[] user : users) {
            model.addRow(user);
        }
    }

    private void statsAnalysis() {
        try {
            int totalBooks = admin.getTotalBooksCount();
            int totalUsers = admin.getTotalUsersCount();
            int borrowedBooks = admin.getBorrowedBooksCount();
            int overdueBooks = admin.getOverdueBooksCount();

            JPanel statsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
            statsPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
            JLabel[] labels = {
                new JLabel("📚 图书总数: " + totalBooks),
                new JLabel("👤 用户总数: " + totalUsers),
                new JLabel("📖 当前借出图书数: " + borrowedBooks),
                new JLabel("⏰ 逾期图书数: " + overdueBooks)
            };
            Font labelFont = new Font("微软雅黑", Font.PLAIN, 18);
            Color labelColor = new Color(30, 60, 120);
            for (JLabel label : labels) {
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
            JOptionPane.showMessageDialog(this, "统计分析失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageBooks() {
        SwingUtilities.invokeLater(() -> {
            try {
                ArrayList<String[]> books = admin.viewAllBooks();
                String[] columnNames = {"ID", "ISBN", "书名", "作者", "是否借出"};
                DefaultTableModel bookModel = new DefaultTableModel(columnNames, 0);
                for (String[] book : books) {
                    bookModel.addRow(book);
                }
                JTable bookTable = new JTable(bookModel);
                bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(bookTable);

                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton refreshBtn = createButton("刷新");
                JButton editBookBtn = createButton("修改图书");
                JButton deleteBookBtn = createButton("删除图书");
                topPanel.add(refreshBtn);
                topPanel.add(editBookBtn);
                topPanel.add(deleteBookBtn);

                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.add(topPanel, BorderLayout.NORTH);
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                JLabel statusLabel = new JLabel("共 " + bookModel.getRowCount() + " 本图书");
                mainPanel.add(statusLabel, BorderLayout.SOUTH);

                refreshBtn.addActionListener(e -> {
                    try {
                        bookModel.setRowCount(0);
                        ArrayList<String[]> updatedBooks = admin.viewAllBooks();
                        for (String[] b : updatedBooks) {
                            bookModel.addRow(b);
                        }
                        statusLabel.setText("共 " + bookModel.getRowCount() + " 本图书");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "刷新失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                editBookBtn.addActionListener(e -> {
                    int row = bookTable.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(this, "请先选择要修改的图书！");
                        return;
                    }
                    try {
                        int bookId = Integer.parseInt(bookModel.getValueAt(row, 0).toString());
                        String oldIsbn = bookModel.getValueAt(row, 1).toString();
                        String oldTitle = bookModel.getValueAt(row, 2).toString();
                        String oldAuthor = bookModel.getValueAt(row, 3).toString();
                        boolean isBorrowed = bookModel.getValueAt(row, 4).toString().equals("true");

                        JTextField isbnField = new JTextField(oldIsbn);
                        JTextField titleField = new JTextField(oldTitle);
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
                            boolean success = admin.updateBookInfo(bookId, isbnField.getText(), titleField.getText(), authorField.getText(), isBorrowed);
                            if (success) {
                                JOptionPane.showMessageDialog(this, "图书信息更新成功！");
                                refreshBtn.doClick();
                            } else {
                                JOptionPane.showMessageDialog(this, "图书不存在或更新失败！");
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "图书管理", Dialog.ModalityType.APPLICATION_MODAL);
                dialog.getContentPane().add(mainPanel);
                dialog.setSize(800, 500);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载图书失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void manageBorrows() {
        SwingUtilities.invokeLater(() -> {
            try {
                JPanel borrowPanel = new JPanel(new BorderLayout(10, 10));
                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                JButton refreshBtn = createButton("刷新");
                JButton viewOverdueBtn = createButton("查看逾期");
                JButton deleteRecordBtn = createButton("删除记录");
                JTextField searchField = new JTextField(15);
                JButton searchBtn = createButton("搜索");

                topPanel.add(refreshBtn);
                topPanel.add(viewOverdueBtn);
                topPanel.add(deleteRecordBtn);
                topPanel.add(new JLabel("用户ID:"));
                topPanel.add(searchField);
                topPanel.add(searchBtn);

                ArrayList<String[]> borrowData = admin.viewAllBorrows();
                String[] cols = {"借阅ID", "用户ID", "图书ID", "借出日期", "应还日期", "状态", "归还日期", "图书状态"};
                DefaultTableModel model = new DefaultTableModel(cols, 0);
                if (borrowData != null) {
                    for (String[] row : borrowData) {
                        model.addRow(row);
                    }
                }
                JTable table = new JTable(model);
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(table);
                JLabel statusLabel = new JLabel("共 " + (borrowData == null ? 0 : borrowData.size()) + " 条借阅记录");

                borrowPanel.add(topPanel, BorderLayout.NORTH);
                borrowPanel.add(scrollPane, BorderLayout.CENTER);
                borrowPanel.add(statusLabel, BorderLayout.SOUTH);

                refreshBtn.addActionListener(e -> {
                    try {
                        model.setRowCount(0);
                        ArrayList<String[]> updatedBorrows = admin.viewAllBorrows();
                        for (String[] row : updatedBorrows) {
                            model.addRow(row);
                        }
                        statusLabel.setText("共 " + updatedBorrows.size() + " 条借阅记录");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "刷新失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                viewOverdueBtn.addActionListener(e -> {
                    try {
                        model.setRowCount(0);
                        ArrayList<String[]> overdueBorrows = admin.viewAllBorrows();
                        int count = 0;
                        for (String[] row : overdueBorrows) {
                            if ("逾期".equals(row[5])) { // 假设状态在第6列
                                model.addRow(row);
                                count++;
                            }
                        }
                        statusLabel.setText("共 " + count + " 条逾期记录");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "查看逾期失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                deleteRecordBtn.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(this, "请先选择要删除的记录");
                        return;
                    }
                    int borrowId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    try {
                        if (admin.deleteBorrowRecord(borrowId)) { // 假设存在此方法
                            refreshBtn.doClick();
                            JOptionPane.showMessageDialog(this, "记录删除成功");
                        } else {
                            JOptionPane.showMessageDialog(this, "删除失败，可能记录不存在");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                searchBtn.addActionListener(e -> {
                    String keyword = searchField.getText().trim();
                    if (keyword.isEmpty()) {
                        refreshBtn.doClick();
                        return;
                    }
                    try {
                        model.setRowCount(0);
                        int count = 0;
                        ArrayList<String[]> allBorrows = admin.viewAllBorrows();
                        for (String[] row : allBorrows) {
                            if (row[1].equals(keyword)) { // 用户ID 在第2列
                                model.addRow(row);
                                count++;
                            }
                        }
                        statusLabel.setText("找到 " + count + " 条记录");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "搜索失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            int row = table.getSelectedRow();
                            if (row != -1) {
                                String details = String.join("\n", (CharSequence[]) model.getDataVector().elementAt(row).toArray(new String[0]));
                                JOptionPane.showMessageDialog(borrowPanel, details, "记录详情", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                });

                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "借阅记录管理", Dialog.ModalityType.APPLICATION_MODAL);
                dialog.getContentPane().add(borrowPanel);
                dialog.setSize(1000, 500);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载借阅数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void manageFines() {
        SwingUtilities.invokeLater(() -> {
            try {
                JPanel finePanel = new JPanel(new BorderLayout(10, 10));
                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                JButton refreshBtn = createButton("刷新");
                JButton fineBtn = createButton("执行罚款");
                topPanel.add(refreshBtn);
                topPanel.add(fineBtn);

                ArrayList<String[]> overdueBorrows = admin.viewAllBorrows(); // 假设使用现有方法筛选逾期记录
                String[] cols = {"借阅ID", "用户ID", "图书ID", "借出日期", "应还日期", "状态", "归还日期", "图书状态"};
                DefaultTableModel model = new DefaultTableModel(cols, 0);
                int overdueCount = 0;
                for (String[] row : overdueBorrows) {
                    if ("逾期".equals(row[5])) { // 筛选逾期记录
                        model.addRow(row);
                        overdueCount++;
                    }
                }
                JTable table = new JTable(model);
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(table);
                JLabel statusLabel = new JLabel("共 " + overdueCount + " 条逾期记录");

                finePanel.add(topPanel, BorderLayout.NORTH);
                finePanel.add(scrollPane, BorderLayout.CENTER);
                finePanel.add(statusLabel, BorderLayout.SOUTH);

                refreshBtn.addActionListener(e -> {
                    try {
                        model.setRowCount(0);
                        ArrayList<String[]> updatedOverdueBorrows = admin.viewAllBorrows();
                        int count = 0;
                        for (String[] row : updatedOverdueBorrows) {
                            if ("逾期".equals(row[5])) {
                                model.addRow(row);
                                count++;
                            }
                        }
                        statusLabel.setText("共 " + count + " 条逾期记录");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "刷新失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                fineBtn.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(this, "请先选择一条逾期记录");
                        return;
                    }
                    int borrowId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    int userId = Integer.parseInt(model.getValueAt(row, 1).toString());
                    try {
                        double amount = admin.calculateFine(borrowId);
                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(this, "该记录无需罚款");
                            return;
                        }
                        int confirm = JOptionPane.showConfirmDialog(this,
                            String.format("用户 %d 逾期应罚款 %.2f 元，确认执行？", userId, amount),
                            "执行罚款", JOptionPane.OK_CANCEL_OPTION);
                        if (confirm == JOptionPane.OK_OPTION) {
                            if (admin.imposeFine(userId, amount)) {
                                double total = admin.getUserTotalFine(userId);
                                JOptionPane.showMessageDialog(this,
                                    String.format("罚款成功！用户累计欠款：%.2f 元", total));
                                refreshBtn.doClick(); // 刷新列表
                            } else {
                                JOptionPane.showMessageDialog(this, "罚款执行失败");
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "罚款失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });

                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "罚款管理", Dialog.ModalityType.APPLICATION_MODAL);
                dialog.getContentPane().add(finePanel);
                dialog.setSize(1000, 500);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载逾期数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void changePassword() {
        String pwd = JOptionPane.showInputDialog(this, "请输入新密码（至少6位）：");
        if (pwd == null || pwd.trim().isEmpty() || pwd.length() < 6) {
            JOptionPane.showMessageDialog(this, "密码不能为空且至少6位！");
            return;
        }
        try {
            admin.changePwd(pwd);
            JOptionPane.showMessageDialog(this, "密码修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "密码修改失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeName() {
        String name = JOptionPane.showInputDialog(this, "请输入新昵称（2-20字符）：");
        if (name == null || name.trim().isEmpty() || name.length() < 2 || name.length() > 20) {
            JOptionPane.showMessageDialog(this, "昵称不能为空且长度在2-20字符之间！");
            return;
        }
        try {
            admin.changeName(name);
            JOptionPane.showMessageDialog(this, "昵称修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "昵称修改失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
       Object ptrunc;
       Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        SwingUtilities.invokeLater(() -> new LoginWindow(new User(), new Admin()).setVisible(true));
    }

    private void systemMaintain() {
        try {
            admin.updateAllOverdueStatus();
            JOptionPane.showMessageDialog(this, "系统维护完成：已更新所有逾期状态", "系统维护", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "系统维护失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        inputPanel.add(new JLabel("图书名称："));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("作者："));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("ISBN："));
        inputPanel.add(isbnField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "添加新图书", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "所有字段均不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                admin.addBook(isbn, title, author);
                JOptionPane.showMessageDialog(this, "图书添加成功！");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "添加图书失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class StyledButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setOpaque(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        int width = c.getWidth(), height = c.getHeight();
        int arc = Math.min(width, height) / 4; // 动态调整圆角
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 3, width - 6, height - 6, arc, arc);
        g2.setColor(c.getBackground());
        g2.fillRoundRect(0, 0, width - 6, height - 6, arc, arc);
        super.paint(g2, c);
        g2.dispose();
    }
}