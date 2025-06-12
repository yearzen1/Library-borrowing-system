import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton userLoginButton;
    private JButton adminLoginButton;
    private JButton teacherLoginButton;
    private JButton registerButton;
    private JButton teacherRegisterButton;
    User user;
    Admin admin;

    public LoginWindow(User user, Admin admin) {
        this.user = user;
        this.admin = admin;
        setupUI();
    }

    private void setupUI() {
        setTitle("图书管理系统 登录界面");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255),
                        getWidth(), getHeight(), new Color(173, 216, 230));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel titleLabel = new JLabel("图书管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 0, 139));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(250, 30));
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        userLoginButton = createStyledButton("学生登录", new Color(70, 130, 180));
        userLoginButton.addActionListener(e -> {
            try {
                attemptLogin(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        adminLoginButton = createStyledButton("管理员登录", new Color(0, 100, 0));
        adminLoginButton.addActionListener(e -> {
            try {
                attemptLogin(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        teacherLoginButton = createStyledButton("老师登录", new Color(255, 165, 0));
        teacherLoginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "登录失败", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                User teacherUser = new User(username, password);
                if (teacherUser.login()) {
                    showUserMainFrame(teacherUser);
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        registerButton = createStyledButton("学生注册", new Color(139, 0, 139));
        registerButton.addActionListener(e -> {
            try {
                showRegisterDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        teacherRegisterButton = createStyledButton("老师注册", new Color(139, 0, 139));
        teacherRegisterButton.addActionListener(e -> {
            try {
                showRegisterDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttonPanel.add(userLoginButton);
        buttonPanel.add(adminLoginButton);
        buttonPanel.add(teacherLoginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(teacherRegisterButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void attemptLogin(boolean isAdmin) throws Exception {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "登录失败", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean loginSuccess = false;

        if (!isAdmin) {
            User user = new User(username, password);
            loginSuccess = user.login();

            if (loginSuccess) {
                showUserMainFrame(user);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Admin admin = new Admin(username, password);
            loginSuccess = admin.login();

            if (loginSuccess) {
                showAdminMainFrame(admin);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUserMainFrame(User user) {
        JFrame frame = new JFrame("用户图书管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new UserMainFrame(user));
        frame.setVisible(true);
        this.dispose();
    }

    private void showAdminMainFrame(Admin admin) {
        JFrame frame = new JFrame("管理员图书管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new AdminMainFrame(admin));
        frame.setVisible(true);
        this.dispose();
    }

    private void showRegisterDialog() throws Exception {
        JTextField regUsernameField = new JTextField();
        JPasswordField regPasswordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("新用户名:"));
        panel.add(regUsernameField);
        panel.add(new JLabel("密码:"));
        panel.add(regPasswordField);
        panel.add(new JLabel("确认密码:"));
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "用户注册",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String username = regUsernameField.getText().trim();
            String pwd = new String(regPasswordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pwd.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                user.setName(username);
                user.setPwd(pwd);
                boolean registrationResult = user.register();

                if (registrationResult) {
                    JOptionPane.showMessageDialog(this, "注册成功！", "注册成功", JOptionPane.INFORMATION_MESSAGE);
                    this.usernameField.setText(username);
                    this.passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "用户已存在", "注册失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "注册过程中发生错误: " + e.getMessage(), "注册失败", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void openMainWindow(User user) {
        SwingUtilities.invokeLater(() -> {
            BookManagementSystem mainSystem = new BookManagementSystem(user);
            mainSystem.setVisible(true);
            this.dispose();
        });
    }

    private void openMainWindow(Admin admin) {
        SwingUtilities.invokeLater(() -> {
            BookManagementSystem mainSystem = new BookManagementSystem(admin);
            mainSystem.setVisible(true);
            this.dispose();
        });
    }
}