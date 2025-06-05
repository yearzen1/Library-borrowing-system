import javax.swing.*;
import java.awt.*;

public class BookManagementSystem extends JFrame {

    // 用户界面构造器
    public BookManagementSystem(User user) {
        setTitle("图书借阅管理系统 - 用户界面");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 这里可以补充具体面板类
        tabbedPane.addTab("所有图书", new JLabel("展示所有图书的列表（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("可借图书", new JLabel("展示可借图书的列表（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("借阅图书", new JLabel("借阅图书功能（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("我的借阅", new JLabel("我的借阅记录（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("个人资料", new JLabel("修改密码/昵称功能（待实现）", SwingConstants.CENTER));

        add(tabbedPane);
    }

    // 管理员界面构造器
    public BookManagementSystem(Admin admin) {
        setTitle("图书借阅管理系统 - 管理员界面");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("所有图书", new JLabel("展示所有图书列表（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("添加图书", new JLabel("添加图书功能（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("修改图书", new JLabel("修改图书功能（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("删除图书", new JLabel("删除图书功能（待实现）", SwingConstants.CENTER));
        tabbedPane.addTab("借阅记录", new JLabel("所有借阅记录查看（待实现）", SwingConstants.CENTER));

        add(tabbedPane);
    }
}
