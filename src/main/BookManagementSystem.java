import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * BookManagementSystem.java
 *
 * 图书借阅管理系统 — 登录后主界面
 *
 * 说明：
 * 1. 使用 CardLayout 管理多个功能面板：修改密码、图书查询、借书管理、还书管理、用户管理、图书管理、借阅统计。
 * 2. 根据当前登录用户 currentUser.isAdmin() 决定哪些模块显示（普通读者 vs 管理员）。
 * 3. 各个模块中已预留“与数据库交互（DAO 层）”的位置（以 TODO 注释标识）。
 * 4. 界面布局使用常见的 BorderLayout、GridBagLayout、GridLayout、FlowLayout。
 * 5. 适当使用 JTable + DefaultTableModel 来展示“图书列表”、“借阅记录”、“用户列表”、“统计报表”等数据，方便后续二次开发。
 */
public class BookManagementSystem extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel       = new JPanel(cardLayout);

    private final User currentUser;
    // 从 LoginWindow 中获取全局的 “users” Map（演示用，后续可替换为真正的数据库）
    private final Map<String, User> users = LoginWindow.getGlobalUserMap();

    // 各卡片面板对应的名称
    private static final String PANEL_CHANGE_PWD    = "修改密码";
    private static final String PANEL_BOOK_QUERY    = "图书查询";
    private static final String PANEL_BORROW        = "借书管理";
    private static final String PANEL_RETURN        = "还书管理";
    private static final String PANEL_USER_MGMT     = "用户管理";       // 仅管理员可见
    private static final String PANEL_BOOK_MGMT     = "图书管理";       // 仅管理员可见
    private static final String PANEL_BORROW_REPORT = "借阅统计";       // 仅管理员可见

    public BookManagementSystem(User user) {
        this.currentUser = user;
        initializeFrame();
        initializeMenu();
        initializeMainCards();
    }

    /** 1. 窗口基础设置 **/
    private void initializeFrame() {
        setTitle("图书借阅管理系统 - 欢迎, " + currentUser.getUsername() +
                (currentUser.isAdmin() ? " (管理员)" : " (读者)"));
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    /** 2. 顶部菜单栏：根据用户权限动态添加菜单项 **/
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();

        // —— “功能” 菜单
        JMenu menuFunction = new JMenu("功能");

        // 普通读者／管理员都可见的菜单项
        JMenuItem miChangePwd   = new JMenuItem("修改密码");
        JMenuItem miBookQuery   = new JMenuItem("图书查询");
        JMenuItem miBorrow      = new JMenuItem("借书管理");
        JMenuItem miReturn      = new JMenuItem("还书管理");

        miChangePwd.addActionListener(e -> cardLayout.show(mainPanel, PANEL_CHANGE_PWD));
        miBookQuery.addActionListener(e -> cardLayout.show(mainPanel, PANEL_BOOK_QUERY));
        miBorrow.addActionListener(e -> cardLayout.show(mainPanel, PANEL_BORROW));
        miReturn.addActionListener(e -> cardLayout.show(mainPanel, PANEL_RETURN));

        menuFunction.add(miChangePwd);
        menuFunction.addSeparator();
        menuFunction.add(miBookQuery);
        menuFunction.add(miBorrow);
        menuFunction.add(miReturn);

        // 如果当前用户是管理员，则额外添加 “用户管理”、“图书管理”、“借阅统计” 三项
        if (currentUser.isAdmin()) {
            menuFunction.addSeparator();
            JMenuItem miUserMgmt    = new JMenuItem("用户管理");
            JMenuItem miBookMgmt    = new JMenuItem("图书管理");
            JMenuItem miBorrowReport= new JMenuItem("借阅统计");

            miUserMgmt.addActionListener(e -> cardLayout.show(mainPanel, PANEL_USER_MGMT));
            miBookMgmt.addActionListener(e -> cardLayout.show(mainPanel, PANEL_BOOK_MGMT));
            miBorrowReport.addActionListener(e -> cardLayout.show(mainPanel, PANEL_BORROW_REPORT));

            menuFunction.add(miUserMgmt);
            menuFunction.add(miBookMgmt);
            menuFunction.add(miBorrowReport);
        }

        menuBar.add(menuFunction);

        // —— “系统” 菜单（注销 / 退出）
        JMenu menuSystem = new JMenu("系统");
        JMenuItem miLogout = new JMenuItem("注销");
        JMenuItem miExit   = new JMenuItem("退出");

        miLogout.addActionListener(e -> {
            // 注销：关闭当前窗口，重新打开登录界面
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginWindow login = new LoginWindow();
                login.setVisible(true);
            });
        });
        miExit.addActionListener(e -> System.exit(0));

        menuSystem.add(miLogout);
        menuSystem.addSeparator();
        menuSystem.add(miExit);
        menuBar.add(menuSystem);

        setJMenuBar(menuBar);
    }

    /** 3. 初始化所有卡片面板（对应 UML 顺序图中的各 participant 功能） **/
    private void initializeMainCards() {
        // 3.1 修改密码 模块 (changepwd)
        mainPanel.add(createChangePwdPanel(), PANEL_CHANGE_PWD);

        // 3.2 图书查询 模块 (BookMgmt)
        mainPanel.add(createBookQueryPanel(), PANEL_BOOK_QUERY);

        // 3.3 借书管理 模块 (BorrowMgmt)
        mainPanel.add(createBorrowPanel(), PANEL_BORROW);

        // 3.4 还书管理 模块 (ReturnMgmt)
        mainPanel.add(createReturnPanel(), PANEL_RETURN);

        // 仅管理员专属模块
        if (currentUser.isAdmin()) {
            // 3.5 用户管理 模块 (UserMgmt)
            mainPanel.add(createUserMgmtPanel(), PANEL_USER_MGMT);

            // 3.6 图书管理 模块 (后台增删改查)
            mainPanel.add(createBookMgmtPanel(), PANEL_BOOK_MGMT);

            // 3.7 借阅统计 模块 (BorrowMgmt 报表)
            mainPanel.add(createBorrowReportPanel(), PANEL_BORROW_REPORT);
        }

        // 默认显示“图书查询”面板
        cardLayout.show(mainPanel, PANEL_BOOK_QUERY);
        add(mainPanel, BorderLayout.CENTER);
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.1 “修改密码” Pane ：对应 UML 中 “2 修改密码” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createChangePwdPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("修改密码"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(10, 10, 10, 10);
        gbc.anchor  = GridBagConstraints.WEST;

        // —— 用户名（只读）
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentUser.getUsername()), gbc);

        // —— 旧密码
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("旧密码："), gbc);
        gbc.gridx = 1;
        JPasswordField txtOldPwd = new JPasswordField(15);
        panel.add(txtOldPwd, gbc);

        // —— 新密码
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("新密码："), gbc);
        gbc.gridx = 1;
        JPasswordField txtNewPwd = new JPasswordField(15);
        panel.add(txtNewPwd, gbc);

        // —— 确认新密码
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("确认密码："), gbc);
        gbc.gridx = 1;
        JPasswordField txtConfirmPwd = new JPasswordField(15);
        panel.add(txtConfirmPwd, gbc);

        // —— 保存按钮
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSave = new JButton("保存修改");
        panel.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String oldPwd     = new String(txtOldPwd.getPassword()).trim();
            String newPwd     = new String(txtNewPwd.getPassword()).trim();
            String confirmPwd = new String(txtConfirmPwd.getPassword()).trim();

            // —— 非空校验
            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "所有密码字段均不能为空", "修改失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // —— 旧密码校验（示例中直接比对明文）
            if (!Objects.equals(currentUser.getPassword(), oldPwd)) {
                JOptionPane.showMessageDialog(this,
                        "旧密码不正确，请重试", "修改失败", JOptionPane.ERROR_MESSAGE);
                txtOldPwd.setText("");
                return;
            }
            // —— 新密码一致性校验
            if (!newPwd.equals(confirmPwd)) {
                JOptionPane.showMessageDialog(this,
                        "两次新密码输入不一致", "修改失败", JOptionPane.ERROR_MESSAGE);
                txtNewPwd.setText("");
                txtConfirmPwd.setText("");
                return;
            }
            // —— TODO：如果使用数据库，请在此调用 UserDAO.updatePassword(userId, newPwdHash)
            // 目前示例直接更新内存 Map 中的 User 对象
            users.put(currentUser.getUsername(),
                    new User(currentUser.getUsername(), newPwd, currentUser.isAdmin()));

            JOptionPane.showMessageDialog(this,
                    "密码修改成功，请重新登录", "成功", JOptionPane.INFORMATION_MESSAGE);

            // 修改密码后强制注销并返回登录
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginWindow login = new LoginWindow();
                login.setVisible(true);
            });
        });

        return panel;
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.2 “图书查询” Pane ：对应 UML 中 “2 图书查询” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createBookQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图书查询"));

        // —— 顶部：关键字搜索
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("关键字（书名/作者）："));
        JTextField txtKeyword = new JTextField(20);
        top.add(txtKeyword);
        JButton btnSearch = new JButton("搜索");
        top.add(btnSearch);
        panel.add(top, BorderLayout.NORTH);

        // —— 中部：图书列表 (JTable)
        String[] columnNames = {"图书ID", "书名", "作者", "出版社", "库存", "状态"};
        Object[][] sampleData = {
                {"B001", "Java 编程思想", "Bruce Eckel", "机械工业出版社", 3, "可借"},
                {"B002", "数据结构与算法", "严蔚敏", "清华大学出版社", 5, "可借"},
                // TODO：实际项目中通过 BookDAO.searchBooksByKeyword(keyword) 加载
        };
        DefaultTableModel model = new DefaultTableModel(sampleData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格只读
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // —— “搜索”按钮动作：查询数据库并刷新表格
        btnSearch.addActionListener(e -> {
            String keyword = txtKeyword.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "请输入关键字进行查询", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO：BookDAO.searchBooksByKeyword(keyword)，获取 List<Book>，
            //       清空 model，再 model.addRow(...) 填充新数据。
            JOptionPane.showMessageDialog(this,
                    "正在查询图书：\"" + keyword + "\" ...", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.3 “借书管理” Pane ：对应 UML 中 “3 借书管理” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("借书管理"));

        // —— 顶部：说明
        JLabel lblInfo = new JLabel("请选择要借阅的图书，然后点击“借阅”", SwingConstants.LEFT);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(lblInfo, BorderLayout.NORTH);

        // —— 中部：可借图书列表 (JTable)
        String[] colNames = {"图书ID", "书名", "作者", "库存"};
        Object[][] sampleData = {
                {"B003", "操作系统原理", "Abraham Silberschatz", 2},
                {"B004", "计算机网络", "Andrew S. Tanenbaum", 4},
                // TODO：实际项目中通过 BookDAO.getAvailableBooks() 加载
        };
        DefaultTableModel model = new DefaultTableModel(sampleData, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // —— 底部：借阅按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JButton btnBorrow = new JButton("借　　阅");
        bottom.add(btnBorrow);
        panel.add(bottom, BorderLayout.SOUTH);

        btnBorrow.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "请先选中一行要借阅的图书", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bookId   = model.getValueAt(row, 0).toString();
            String bookName = model.getValueAt(row, 1).toString();
            int available   = Integer.parseInt(model.getValueAt(row, 3).toString());

            // —— 库存检查
            if (available <= 0) {
                JOptionPane.showMessageDialog(this,
                        "该图书库存不足，无法借阅", "借阅失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // TODO：对接数据库事务
            // Connection conn = DataSource.getConnection();
            // conn.setAutoCommit(false);
            // BorrowDAO.insertBorrowRecord(currentUser.getId(), bookId, borrowDate, dueDate, conn);
            // BookDAO.updateAvailableCount(bookId, available - 1, conn);
            // conn.commit();

            // 提示借阅成功
            JOptionPane.showMessageDialog(this,
                    String.format("已成功借阅 [%s] %s", bookId, bookName),
                    "借阅成功", JOptionPane.INFORMATION_MESSAGE);

            // 更新表格该行库存：available - 1
            model.setValueAt(available - 1, row, 3);
        });

        return panel;
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.4 “还书管理” Pane ：对应 UML 中 “3 还书管理” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("还书管理"));

        // —— 顶部：说明
        JLabel lblInfo = new JLabel("请选择要归还的图书，然后点击“归还”", SwingConstants.LEFT);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(lblInfo, BorderLayout.NORTH);

        // —— 中部：已借未还记录列表 (JTable)
        String[] colNames = {"借阅ID", "图书ID", "书名", "借阅日期", "应还日期"};
        Object[][] sampleData = {
                {"L001", "B005", "算法导论", "2025-05-20", "2025-06-20"},
                {"L002", "B006", "操作系统", "2025-05-28", "2025-06-28"},
                // TODO：实际项目中通过 BorrowDAO.getUnreturnedByUser(currentUserId) 加载
        };
        DefaultTableModel model = new DefaultTableModel(sampleData, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // —— 底部：归还按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JButton btnReturn = new JButton("归　　还");
        bottom.add(btnReturn);
        panel.add(bottom, BorderLayout.SOUTH);

        btnReturn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "请先选中一行要归还的记录", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String lendId   = model.getValueAt(row, 0).toString();
            String bookId   = model.getValueAt(row, 1).toString();
            String bookName = model.getValueAt(row, 2).toString();

            // TODO：对接数据库事务
            // Connection conn = DataSource.getConnection();
            // conn.setAutoCommit(false);
            // BorrowDAO.markAsReturned(lendId, returnDate, conn);
            // BookDAO.incrementAvailableCount(bookId, conn);
            // conn.commit();

            JOptionPane.showMessageDialog(this,
                    String.format("已成功归还 [%s] %s", lendId, bookName),
                    "归还成功", JOptionPane.INFORMATION_MESSAGE);

            // 从表格中移除该行
            model.removeRow(row);
        });

        return panel;
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.5 “用户管理” Pane ：仅管理员可见，对应 UML 中 “2 用户管理” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createUserMgmtPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("用户管理（仅管理员）"));

        // —— 顶部：添加/修改/删除 用户 按钮
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAdd    = new JButton("添加用户");
        JButton btnEdit   = new JButton("修改用户");
        JButton btnDelete = new JButton("删除用户");
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        panel.add(top, BorderLayout.NORTH);

        // —— 中部：用户列表 JTable
        String[] colNames = {"用户名", "是否管理员"};
        Object[][] sampleData = {
                {"admin", true},
                {"user1", false},
                {"user2", false}
                // TODO：实际项目中通过 UserDAO.getAllUsers() 加载
        };
        DefaultTableModel model = new DefaultTableModel(sampleData, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // —— 添加用户
        btnAdd.addActionListener(e -> {
            JTextField txtUsername = new JTextField();
            JPasswordField txtPwd   = new JPasswordField();
            JCheckBox chkAdmin      = new JCheckBox("是否管理员");

            JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
            p.add(new JLabel("用户名："));    p.add(txtUsername);
            p.add(new JLabel("密码 (示例明文)：")); p.add(txtPwd);
            p.add(chkAdmin);

            int res = JOptionPane.showConfirmDialog(
                    this, p, "添加新用户", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String u       = txtUsername.getText().trim();
                String pw      = new String(txtPwd.getPassword());
                boolean asAdmin= chkAdmin.isSelected();

                // —— 非空校验
                if (u.isEmpty() || pw.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "用户名或密码不能为空", "添加失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // —— 唯一性校验
                if (users.containsKey(u)) {
                    JOptionPane.showMessageDialog(this,
                            "用户名已存在", "添加失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // TODO：调用 UserDAO.insertUser(u, pwHash, asAdmin)
                users.put(u, new User(u, pw, asAdmin));  // 更新内存示例
                model.addRow(new Object[]{u, asAdmin});
                JOptionPane.showMessageDialog(this,
                        "用户添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // —— 修改用户
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "请先选中一行要修改的用户", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String oldUsername = model.getValueAt(row, 0).toString();
            boolean oldIsAdmin = Boolean.parseBoolean(model.getValueAt(row, 1).toString());

            JTextField txtUsername = new JTextField(oldUsername);
            JCheckBox chkAdmin     = new JCheckBox("是否管理员", oldIsAdmin);

            JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
            p.add(new JLabel("用户名：")); p.add(txtUsername);
            p.add(chkAdmin);

            int res = JOptionPane.showConfirmDialog(
                    this, p, "修改用户", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String newUsername = txtUsername.getText().trim();
                boolean newIsAdmin = chkAdmin.isSelected();

                if (newUsername.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "用户名不能为空", "修改失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 如果改了用户名，需要检查新用户名是否已存在
                if (!newUsername.equals(oldUsername) && users.containsKey(newUsername)) {
                    JOptionPane.showMessageDialog(this,
                            "用户名已存在", "修改失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // TODO：调用 UserDAO.updateUser(oldUsername, newUsername, newIsAdmin)
                User oldUser = users.remove(oldUsername);
                users.put(newUsername, new User(newUsername, oldUser.getPassword(), newIsAdmin));

                model.setValueAt(newUsername, row, 0);
                model.setValueAt(newIsAdmin, row, 1);
                JOptionPane.showMessageDialog(this,
                        "用户修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // —— 删除用户
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "请先选中一行要删除的用户", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = model.getValueAt(row, 0).toString();
            // 禁止删除自己
            if (username.equals(currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this,
                        "无法删除当前登录的用户", "操作禁止", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确认删除用户：\"" + username + "\"？",
                    "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // TODO：调用 UserDAO.deleteUser(username)
                users.remove(username);
                model.removeRow(row);
                JOptionPane.showMessageDialog(this,
                        "用户删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.6 “图书管理” Pane ：仅管理员可见，对应 UML 中 “Admin → BookMgmt” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createBookMgmtPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图书管理（仅管理员）"));

        // —— 顶部：添加/修改/删除 图书 按钮
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAdd    = new JButton("添加图书");
        JButton btnEdit   = new JButton("修改图书");
        JButton btnDelete = new JButton("删除图书");
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        panel.add(top, BorderLayout.NORTH);

        // —— 中部：图书列表 JTable
        String[] colNames = {"图书ID", "书名", "作者", "出版社", "总库存", "可借数量", "状态"};
        Object[][] sampleData = {
                {"B001", "Java 编程思想", "Bruce Eckel", "机械工业出版社", 5, 3, "可借"},
                {"B002", "数据结构与算法", "严蔚敏", "清华大学出版社", 7, 5, "可借"}
                // TODO：实际项目中通过 BookDAO.getAllBooks() 加载
        };
        DefaultTableModel model = new DefaultTableModel(sampleData, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // —— 添加图书
        btnAdd.addActionListener(e -> {
            JTextField txtBookId    = new JTextField();
            JTextField txtTitle     = new JTextField();
            JTextField txtAuthor    = new JTextField();
            JTextField txtPublisher = new JTextField();
            JTextField txtTotal     = new JTextField();

            JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
            p.add(new JLabel("图书ID："));    p.add(txtBookId);
            p.add(new JLabel("书名："));      p.add(txtTitle);
            p.add(new JLabel("作者："));      p.add(txtAuthor);
            p.add(new JLabel("出版社："));    p.add(txtPublisher);
            p.add(new JLabel("总库存："));    p.add(txtTotal);

            int res = JOptionPane.showConfirmDialog(
                    this, p, "添加新图书", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String id   = txtBookId.getText().trim();
                String name = txtTitle.getText().trim();
                String au   = txtAuthor.getText().trim();
                String pub  = txtPublisher.getText().trim();
                int total;
                try {
                    total = Integer.parseInt(txtTotal.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "库存必须为整数", "失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (id.isEmpty() || name.isEmpty() || au.isEmpty() || pub.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "请填写完整信息", "失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // TODO：调用 BookDAO.exists(id) 判断是否已有该图书
                boolean exists = false; // 示例中直接设为 false
                if (exists) {
                    JOptionPane.showMessageDialog(this,
                            "图书ID已存在", "失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // TODO：BookDAO.insertBook(id, name, au, pub, total, total, "可借");
                model.addRow(new Object[]{id, name, au, pub, total, total, "可借"});
                JOptionPane.showMessageDialog(this,
                        "图书添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // —— 修改图书
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "请先选中一行要修改的图书", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String oldBookId    = model.getValueAt(row, 0).toString();
            String oldTitle     = model.getValueAt(row, 1).toString();
            String oldAuthor    = model.getValueAt(row, 2).toString();
            String oldPublisher = model.getValueAt(row, 3).toString();
            String oldTotal     = model.getValueAt(row, 4).toString();

            JTextField txtTitle     = new JTextField(oldTitle);
            JTextField txtAuthor    = new JTextField(oldAuthor);
            JTextField txtPublisher = new JTextField(oldPublisher);
            JTextField txtTotal     = new JTextField(oldTotal);

            JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
            p.add(new JLabel("书名："));      p.add(txtTitle);
            p.add(new JLabel("作者："));      p.add(txtAuthor);
            p.add(new JLabel("出版社："));    p.add(txtPublisher);
            p.add(new JLabel("总库存："));    p.add(txtTotal);

            int res = JOptionPane.showConfirmDialog(
                    this, p, "修改图书 [" + oldBookId + "]", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String name = txtTitle.getText().trim();
                String au   = txtAuthor.getText().trim();
                String pub  = txtPublisher.getText().trim();
                int total;
                try {
                    total = Integer.parseInt(txtTotal.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "库存必须为整数", "失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (name.isEmpty() || au.isEmpty() || pub.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "请填写完整信息", "失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // TODO：BookDAO.updateBook(oldBookId, name, au, pub, total, newAvailable, newStatus);
                // 这里暂时将“总库存”更新到第 5 列，但不更新“可借数量”和“状态”
                model.setValueAt(name, row, 1);
                model.setValueAt(au, row, 2);
                model.setValueAt(pub, row, 3);
                model.setValueAt(total, row, 4);
                JOptionPane.showMessageDialog(this,
                        "图书修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // —— 删除图书
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "请先选中一行要删除的图书", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bookId = model.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确认删除图书：\"" + bookId + "\"？",
                    "确认删除", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                // TODO：BookDAO.deleteBook(bookId)
                model.removeRow(row);
                JOptionPane.showMessageDialog(this,
                        "图书删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }

    //——————————————————————————————————————————————————————————————————————————
    // 3.7 “借阅统计” Pane ：仅管理员可见，对应 UML 中 “Admin → BorrowMgmt → FileSystem 读取借阅记录” 模块
    //——————————————————————————————————————————————————————————————————————————
    private JPanel createBorrowReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("借阅统计（仅管理员）"));

        // —— 顶部：筛选条件（可选：按时间段 / 按图书 / 按用户）
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("查询条件："));
        // TODO：可以添加 JComboBox<图书>、JDatePicker 等组件
        JButton btnGenerate = new JButton("生成报表");
        top.add(btnGenerate);
        panel.add(top, BorderLayout.NORTH);

        // —— 中部：统计结果 (JTable)
        String[] colNames = {"图书ID", "书名", "借阅次数"};
        Object[][] sampleData = {
                {"B001", "Java 编程思想", 120},
                {"B002", "数据结构与算法", 95}
                // TODO：真实项目通过 BorrowDAO.getBorrowStatistics(startDate, endDate) 加载
        };
        DefaultTableModel model = new DefaultTableModel(sampleData, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        btnGenerate.addActionListener(e -> {
            // TODO：获取筛选条件，调用 BorrowDAO.getBorrowStatistics(...)
            JOptionPane.showMessageDialog(this,
                    "正在生成借阅统计报表 ...", "提示", JOptionPane.INFORMATION_MESSAGE);
            // TODO：清空 model，插入新行
        });

        return panel;
    }
}
