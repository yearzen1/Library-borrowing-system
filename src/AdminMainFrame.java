import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
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

        JButton manageUsersBtn = createButton("用户管理");
        JButton manageBooksBtn = createButton("图书管理");
        JButton manageBorrowsBtn = createButton("借阅记录管理");
        JButton statsAnalysisBtn = createButton("统计分析");
        JButton systemMaintainBtn = createButton("系统维护");
        JButton changePwdBtn = createButton("修改密码");
        JButton changeNameBtn = createButton("修改昵称");
        JButton logoutBtn = createButton("退出登录");
        JButton addBookBtn = createButton("添加图书");

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
        logoutBtn.addActionListener(e -> logout());
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
        JOptionPane.showMessageDialog(this, "进入用户管理界面（待实现）");
    }

   private void manageBooks() {
    try {
        ArrayList<String[]> books = admin. viewAllBooks();
        if (books == null || books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "当前没有图书记录");
            return;
        }

        // 自定义列名
        String[] columnNames = {"id", "isbn","书名", "作者", "借阅状态"};
        JTable table = new JTable(books.toArray(new Object[0][]), columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "图书列表", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().add(scrollPane);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "查询图书失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }
}

    private void manageBorrows() {
        JOptionPane.showMessageDialog(this, "进入借阅记录管理界面（待实现）");
    }

    private void statsAnalysis() {
        JOptionPane.showMessageDialog(this, "进入统计分析界面（待实现）");
    }

    private void systemMaintain() {
        JOptionPane.showMessageDialog(this, "进入系统维护界面（待实现）");
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

    private void logout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        JOptionPane.showMessageDialog(null, "已退出登录，请重新登录。");
    }

    private void displayTable(ArrayList<String[]> data, String title) {
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无结果。");
            return;
        }

        String[] columnNames = new String[data.get(0).length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = "列 " + (i + 1);
        }

        JTable table = new JTable(data.toArray(new Object[0][]), columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().add(scrollPane);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ✅ 添加图书功能实现
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
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();


            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() ) {
                JOptionPane.showMessageDialog(this, "所有字段均不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {

                admin.addBook(isbn,title, author);
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
