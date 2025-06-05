import javax.swing.*;
import java.awt.*;
public class BookManagementSystem extends JFrame {

    public BookManagementSystem(User user) {
        setTitle("图书借阅管理系统 - 用户界面");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcomeLabel = new JLabel("欢迎您，" + user.getName() + "（用户）", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        welcomeLabel.setForeground(new Color(30, 144, 255)); // DodgerBlue

        UserMainFrame userPanel = new UserMainFrame(user);

        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(userPanel, BorderLayout.CENTER);
    }

    public BookManagementSystem(Admin admin) {
        setTitle("图书借阅管理系统 - 管理员界面");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcomeLabel = new JLabel("欢迎您，" + admin.getName() + "（管理员）", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        welcomeLabel.setForeground(new Color(220, 20, 60)); // Crimson

        AdminMainFrame adminPanel = new AdminMainFrame(admin);

        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(adminPanel, BorderLayout.CENTER);
    }
}
