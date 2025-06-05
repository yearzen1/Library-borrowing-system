import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton userLoginButton;
    private JButton adminLoginButton;
    private JButton registerButton;
    User user;
    Admin admin;

    public LoginWindow(User user, Admin admin) {
        this.user = user;
        this.admin = admin;
        setupUI();
    }

    private void setupUI() {
        setTitle("图书管理系统 登录界面");
        setSize(450, 350);
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
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 0, 139));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 30));

        userLoginButton = createStyledButton("用户登录", new Color(70, 130, 180));
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

        registerButton = createStyledButton("用户注册", new Color(139, 0, 139));
        registerButton.addActionListener(e -> {
            try {
                showRegisterDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttonPanel.add(userLoginButton);
        buttonPanel.add(adminLoginButton);
        buttonPanel.add(registerButton);

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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
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

        if (isAdmin) {
            admin.setName(username);
            admin.setPwd(password);
            if (!admin.login()) {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            openMainWindow(admin);
        } else {
            user.setName(username);
            user.setPwd(password);
            if (!user.login()) {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            openMainWindow(user);
        }
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
