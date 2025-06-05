import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class UserMainFrame extends JPanel {
    private User user;

    public UserMainFrame(User user) {
        this.user = user;

        // 设置主面板背景和内边距
        setBackground(new Color(245, 250, 255));
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // 顶部欢迎语
        JLabel welcomeLabel = new JLabel("欢迎你，" + user.getName() + "！");
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(30, 60, 120));
        add(welcomeLabel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        buttonPanel.setOpaque(false);  // 背景透明

        String[] buttonTexts = {
            "查看所有图书", "查看可借图书", "搜索图书", "借阅图书",
            "归还图书", "查看我的借阅记录", "修改密码", "修改昵称"
        };

        JButton[] buttons = new JButton[buttonTexts.length];

        for (int i = 0; i < buttonTexts.length; i++) {
            buttons[i] = createStyledButton(buttonTexts[i]);
            buttonPanel.add(buttons[i]);
        }

        // 添加按钮面板居中显示
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // 绑定事件
        buttons[0].addActionListener(e -> showBooks("all"));
        buttons[1].addActionListener(e -> showBooks("available"));
        buttons[2].addActionListener(e -> searchBook());
        buttons[3].addActionListener(e -> borrowBook());
        buttons[4].addActionListener(e -> returnBook());
        buttons[5].addActionListener(e -> showMyBorrows());
        buttons[6].addActionListener(e -> changePassword());
        buttons[7].addActionListener(e -> changeName());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(200, 220, 240));
        button.setForeground(Color.DARK_GRAY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 180, 200)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        return button;
    }

    // 以下逻辑保持不变
    private void showBooks(String type) {
        try {
            ArrayList<String[]> books;
            if ("all".equals(type)) {
                books = user.viewAllBooks();
            } else {
                books = user.viewAviliableBooks();
            }
            displayTable(books, "图书列表 - " + ("all".equals(type) ? "所有图书" : "可借图书"));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载图书列表失败！");
        }
    }

    private void searchBook() {
        String name = JOptionPane.showInputDialog(this, "请输入书名：");
        if (name == null || name.trim().isEmpty()) return;
        try {
            ArrayList<String[]> result = user.viewSpecificBook(name);
            displayTable(result, "搜索结果");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "搜索图书失败！");
        }
    }

    private void borrowBook() {
        String input = JOptionPane.showInputDialog(this, "请输入要借阅图书的ID：");
        if (input == null) return;
        try {
            int id = Integer.parseInt(input);
            boolean success = user.borrowBooks(id);
            JOptionPane.showMessageDialog(this, success ? "借阅成功！" : "借阅失败，图书可能已借出。");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字ID！");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "借阅操作异常！");
        }
    }

    private void returnBook() {
        String input = JOptionPane.showInputDialog(this, "请输入要归还图书的ID：");
        if (input == null) return;
        try {
            int id = Integer.parseInt(input);
            boolean success = user.returnBooks(id);
            JOptionPane.showMessageDialog(this, success ? "归还成功！" : "归还失败，请确认图书ID及借阅状态。");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字ID！");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "归还操作异常！");
        }
    }

    private void showMyBorrows() {
        try {
            ArrayList<String[]> borrows = user.viewMyBorrows();
            displayTable(borrows, "我的借阅记录");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载借阅记录失败！");
        }
    }

    private void changePassword() {
        String pwd = JOptionPane.showInputDialog(this, "请输入新密码：");
        if (pwd == null || pwd.trim().isEmpty()) return;
        try {
            user.changePwd(pwd);
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
            user.changeName(name);
            JOptionPane.showMessageDialog(this, "昵称修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "昵称修改失败！");
        }
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
}
