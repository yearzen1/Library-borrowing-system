
import javax.swing.*;

import java.awt.*; 
import java.util.HashMap; 
import java.util.Map; 

public class LoginWindow extends JFrame {
    private Map<String, User> users = new HashMap<>();
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton userLoginButton;
    private JButton adminLoginButton;
    private JButton registerButton;
    User user;
    Admin admin;

    public LoginWindow(User user,Admin admin)throws Exception {
       this. user=user;
       this.admin=admin;
        setupUI();
    }

    private void setupUI()throws Exception {
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
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,  RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255),
                        getWidth(), getHeight(), new Color(173, 216, 230));
                g2d.setPaint(gp); 
                g2d.fillRect(0,  0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,  20, 20, 20));
        add(mainPanel);

        // 标题
        JLabel titleLabel = new JLabel("图书管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new  Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new  Color(0, 0, 139));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0,  0, 20, 0));
        mainPanel.add(titleLabel,  BorderLayout.NORTH);

        // 表单
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);

        // 用户名
        gbc.gridx  = 0; gbc.gridy  = 0;
        gbc.anchor  = GridBagConstraints.LINE_END;
        formPanel.add(new  JLabel("用户名:"), gbc);

        gbc.gridx  = 1;
        gbc.anchor  = GridBagConstraints.LINE_START;
        usernameField = new JTextField(15);
        formPanel.add(usernameField,  gbc);

        // 密码
        gbc.gridx  = 0; gbc.gridy  = 1;
        gbc.anchor  = GridBagConstraints.LINE_END;
        formPanel.add(new  JLabel("密码:"), gbc);

        gbc.gridx  = 1;
        gbc.anchor  = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField,  gbc);

        mainPanel.add(formPanel,  BorderLayout.CENTER);

        // 按钮
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setOpaque(false); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,  30, 0, 30));

        userLoginButton = createStyledButton("用户登录", new Color(70, 130, 180));
        userLoginButton.addActionListener(e  -> {
            try {
                attemptLogin(false);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        adminLoginButton = createStyledButton("管理员登录", new Color(0, 100, 0));
        adminLoginButton.addActionListener(e  -> {
            try {
                attemptLogin(true);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        registerButton = createStyledButton("用户注册", new Color(139, 0, 139));
        registerButton.addActionListener(e  -> {
            try {
                showRegisterDialog();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        buttonPanel.add(userLoginButton); 
        buttonPanel.add(adminLoginButton); 
        buttonPanel.add(registerButton); 

        mainPanel.add(buttonPanel,  BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor)
    {
        JButton button = new JButton(text);
        button.setFont(new  Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor); 
        button.setForeground(Color.WHITE); 
        button.setFocusPainted(false); 
        button.setBorder(BorderFactory.createCompoundBorder( 
            BorderFactory.createLineBorder(bgColor.darker(),  1),
            BorderFactory.createEmptyBorder(5,  15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
        
        button.addMouseListener(new  java.awt.event.MouseAdapter()  {
            public void mouseEntered(java.awt.event.MouseEvent  evt) {
                button.setBackground(bgColor.darker()); 
            }
            public void mouseExited(java.awt.event.MouseEvent  evt) {
                button.setBackground(bgColor); 
            }
        });
        return button;
    }
    private void attemptLogin(boolean isAdmin) throws Exception 
    {
        if(isAdmin==false)
        {
            String username = usernameField.getText().trim(); 
            String password = new String(passwordField.getPassword()); 
            user.setName(username);
            user.setPwd(password);

        if (username.isEmpty()  || password.isEmpty())  
        {
            JOptionPane.showMessageDialog(this,  "用户名和密码不能为空", "登录失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean result2=user.login();
        if (result2==false)
        {
            JOptionPane.showMessageDialog(this,  "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        }
        else
        {
        String username = usernameField.getText().trim(); 
        String password = new String(passwordField.getPassword()); 
        admin.setName(username);
        admin.setPwd(password);

        if (username.isEmpty()  || password.isEmpty())  
        {
            JOptionPane.showMessageDialog(this,  "用户名和密码不能为空", "登录失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean result2=admin.login();
        if (result2==false) 
        {
            JOptionPane.showMessageDialog(this,  "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        }
        // openMainWindow(user);打开窗口
    }   
    private void showRegisterDialog() throws Exception
    {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new  JLabel("新用户名:"));
        panel.add(usernameField); 
        panel.add(new  JLabel("密码:"));
        panel.add(passwordField); 
        panel.add(new  JLabel("确认密码:"));
        panel.add(confirmField); 

        int result = JOptionPane.showConfirmDialog( 
            this, panel, "用户注册", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );

    if (result == JOptionPane.OK_OPTION) {
        String username = usernameField.getText().trim(); 
        String pwd = new String(passwordField.getPassword());  
        String confirm = new String(confirmField.getPassword());  
        
        // 输入验证 
        if (username.isEmpty()  || pwd.isEmpty())  {
            JOptionPane.showMessageDialog(this,  "用户名和密码不能为空", "注册失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (!pwd.equals(confirm))  {
            JOptionPane.showMessageDialog(this,  "两次输入的密码不一致", "注册失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        try {
            user.setName(username); 
            user.setPwd(pwd); 
            boolean registrationResult = user.register(); 
            
            if (registrationResult) {
                // 注册成功后操作 
                JOptionPane.showMessageDialog(this,  "注册成功！", "注册成功", JOptionPane.INFORMATION_MESSAGE);
                
                // 可选：自动填充登录表单 
                this.usernameField.setText(username); 
                this.passwordField.setText(""); 
                
                return; // 明确返回 
            } else {
                JOptionPane.showMessageDialog(this,  "用户已存在", "注册失败", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,  "注册过程中发生错误: " + e.getMessage(),  "注册失败", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }
}
    }
    private void openMainWindow(User user) 
    {
        SwingUtilities.invokeLater(()  -> 
        {
            BookManagementSystem mainSystem = new BookManagementSystem(user);
            mainSystem.setVisible(true); 
            this.dispose(); 
        });
    }
}