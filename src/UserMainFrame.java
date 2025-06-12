import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class UserMainFrame extends JPanel {
    private User user;

    public UserMainFrame(User user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        // 主面板
        setBackground(new Color(245, 250, 255));
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // 顶部：欢迎语
        JLabel welcome = new JLabel("欢迎你，" + user.getName() + "！", SwingConstants.CENTER);
        welcome.setFont(new Font("微软雅黑", Font.BOLD, 26));
        welcome.setForeground(new Color(25, 55, 110));
        add(welcome, BorderLayout.NORTH);

        // 中部：卡片式功能区
        JPanel grid = new JPanel(new GridLayout(3,3,20,20));
        grid.setOpaque(false);
        String[] texts = {
            "查看所有图书", "查看可借图书", "搜索图书",
            "借阅图书",   "归还图书",     "续借图书",
            "我的借阅记录", "修改密码",     "修改昵称"
        };
        for (String t : texts) {
            grid.add(createCardButton(t));
        }
        add(grid, BorderLayout.CENTER);

        // 底部：退出登录
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        JButton logout = new JButton("退出登录");
        logout.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        logout.setBackground(new Color(200, 60, 60));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.setPreferredSize(new Dimension(100, 36));
        logout.setBorder(new LineBorder(new Color(170, 30, 30), 1, true));
        logout.addActionListener(e -> logout());
        south.add(logout);
        add(south, BorderLayout.SOUTH);

        // 事件绑定
        Component[] comps = grid.getComponents();
        ((JButton)comps[0]).addActionListener(e -> showBooks("all"));
        ((JButton)comps[1]).addActionListener(e -> showBooks("available"));
        ((JButton)comps[2]).addActionListener(e -> searchBook());
        ((JButton)comps[3]).addActionListener(e -> borrowBook());
        ((JButton)comps[4]).addActionListener(e -> returnBook());
        ((JButton)comps[5]).addActionListener(e -> renewBook());
        ((JButton)comps[6]).addActionListener(e -> showMyBorrows());
        ((JButton)comps[7]).addActionListener(e -> changePassword());
        ((JButton)comps[8]).addActionListener(e -> changeName());
    }

    private JButton createCardButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(25, 55, 110));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(20,10,20,10));
        btn.setUI(new StyledButtonUI());
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(220,230,250)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private void showBooks(String type) {
        try {
            ArrayList<String[]> list = "all".equals(type) ?
                user.viewAllBooks() : user.viewAviliableBooks();
            displayTable(list, "图书列表 - " + ("all".equals(type)?"所有图书":"可借图书"));
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "加载图书列表失败！");
        }
    }

    private void searchBook() {
        String name = JOptionPane.showInputDialog(this, "请输入书名：");
        if (name == null||name.trim().isEmpty()) return;
        try {
            ArrayList<String[]> res = user.viewSpecificBook(name.trim());
            displayTable(res, "搜索结果");
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "搜索图书失败！");
        }
    }

    private void borrowBook() {
        String in = JOptionPane.showInputDialog(this, "请输入要借阅图书的ID：");
        if (in==null) return;
        try {
            int id = Integer.parseInt(in.trim());
            boolean ok = user.borrowBooks(id);
            JOptionPane.showMessageDialog(this, ok?"借阅成功！":"借阅失败，可能已被借出。");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字ID！");
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "借阅操作异常！");
        }
    }

    private void returnBook() {
        String in = JOptionPane.showInputDialog(this, "请输入要归还图书的ID：");
        if (in==null) return;
        try {
            int id = Integer.parseInt(in.trim());
            boolean ok = user.returnBooks(id);
            JOptionPane.showMessageDialog(this, ok?"归还成功！":"归还失败，请确认状态。");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字ID！");
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "归还操作异常！");
        }
    }

    private void renewBook() {
        String in = JOptionPane.showInputDialog(this, "请输入要续借图书的ID：");
        if (in==null||in.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(in.trim());
            boolean ok = user.renewBook(id);
            JOptionPane.showMessageDialog(this, ok?"续借成功！到期日已延长30天。":"续借失败：可能已逾期或未借阅。");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效数字ID！");
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "续借操作异常！");
        }
    }

    private void showMyBorrows() {
        try {
            ArrayList<String[]> list = user.viewMyBorrows();
            displayTable(list, "我的借阅记录");
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "加载借阅记录失败！");
        }
    }

    private void changePassword() {
        String pwd = JOptionPane.showInputDialog(this, "请输入新密码：");
        if (pwd==null||pwd.trim().isEmpty()) return;
        try { user.changePwd(pwd.trim()); JOptionPane.showMessageDialog(this,"密码修改成功！"); }
        catch(Exception e){e.printStackTrace(); JOptionPane.showMessageDialog(this,"密码修改失败！");}
    }

    private void changeName() {
        String nm = JOptionPane.showInputDialog(this, "请输入新昵称：");
        if (nm==null||nm.trim().isEmpty()) return;
        try { user.changeName(nm.trim()); JOptionPane.showMessageDialog(this,"昵称修改成功！"); }
        catch(Exception e){e.printStackTrace(); JOptionPane.showMessageDialog(this,"昵称修改失败！");}
    }

    private void displayTable(ArrayList<String[]> data, String title) {
        if (data==null||data.isEmpty()) {
            JOptionPane.showMessageDialog(this,"无结果。"); return;
        }
        String[] colNames;
        switch(data.get(0).length) {
            case 5: colNames = new String[]{"图书ID","ISBN","书名","作者","借阅状态"}; break;
            case 6: colNames = new String[]{"借阅ID","图书ID","书名","借阅日期","归还日期","是否归还"}; break;
            default:
                colNames = new String[data.get(0).length];
                for(int i=0;i<colNames.length;i++) colNames[i] = "列"+(i+1);
        }
        JTable table = new JTable(data.toArray(new Object[0][]), colNames);
        JScrollPane sp = new JScrollPane(table);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.getContentPane().add(sp);
        dlg.setSize(700,400);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void logout() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if(w!=null) w.dispose();
        SwingUtilities.invokeLater(() -> new LoginWindow(new User(), new Admin()).setVisible(true));
    }
}

// 自定义按钮 UI，绘制简单阴影和圆角
class StyledButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
    @Override public void installUI(JComponent c) {
        super.installUI(c);
        c.setOpaque(false);
        c.setBorder(new EmptyBorder(0,0,0,0));
    }
    @Override public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = c.getWidth(), h = c.getHeight();
        // 绘制阴影
        g2.setColor(new Color(0,0,0,30));
        g2.fillRoundRect(4,4,w-8,h-8,16,16);
        // 绘制背景
        g2.setColor(c.getBackground());
        g2.fillRoundRect(0,0,w-8,h-8,16,16);
        super.paint(g2, c);
        g2.dispose();
    }
}
