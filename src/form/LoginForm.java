package form;

import service.AuthService;
import entity.User;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private AuthService authService = new AuthService();

    public LoginForm() {

        FlatLightLaf.setup();

        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

        setTitle("GLOBAL HRIS - Executive Access");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("SIGN IN TO SYSTEM");

        initTitanUI();

        this.getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(e -> handleLogin());

        pack();
        setLocationRelativeTo(null);
    }

    private void initTitanUI() {

        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setPreferredSize(new Dimension(1200, 760));

        // =====================================================
        // LEFT PANEL
        // =====================================================

        JPanel leftPanel = new JPanel(new GridBagLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY
                );

                GradientPaint gp = new GradientPaint(
                        0, 0,
                        new Color(15, 23, 42),
                        0, getHeight(),
                        new Color(30, 41, 59)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(-120, -120, 320, 320);

                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(160, 430, 380, 380);
            }
        };

        leftPanel.setBorder(new EmptyBorder(80, 70, 80, 70));

        GridBagConstraints gbcL = new GridBagConstraints();

        gbcL.gridx = 0;
        gbcL.anchor = GridBagConstraints.WEST;

        JLabel lblMini = new JLabel("● HR MANAGEMENT SYSTEM");

        lblMini.setForeground(new Color(96, 165, 250));
        lblMini.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblTitle = new JLabel(
                "<html>"
                + "<div style='color:white; line-height:115%;'>"
                + "<span style='font-size:58px; font-weight:700;'>"
                + "SMART<br>HRIS<br>PORTAL"
                + "</span>"
                + "</div>"
                + "</html>"
        );

        JLabel lblDesc = new JLabel(
                "<html>"
                + "<div style='width:340px; color:#CBD5E1; font-size:14px; line-height:170%;'>"
                + "Modern enterprise platform for employee management, payroll administration, "
                + "attendance tracking, workforce analytics, and strategic HR operations."
                + "</div>"
                + "</html>"
        );

        JPanel statPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statPanel.setOpaque(false);

        statPanel.add(createStatCard("250+", "Employees"));
        statPanel.add(createStatCard("98%", "Efficiency"));
        statPanel.add(createStatCard("24/7", "Monitoring"));

        gbcL.gridy = 0;
        gbcL.insets = new Insets(0, 0, 25, 0);
        leftPanel.add(lblMini, gbcL);

        gbcL.gridy = 1;
        gbcL.insets = new Insets(0, 0, 30, 0);
        leftPanel.add(lblTitle, gbcL);

        gbcL.gridy = 2;
        gbcL.insets = new Insets(0, 0, 45, 0);
        leftPanel.add(lblDesc, gbcL);

        gbcL.gridy = 3;
        leftPanel.add(statPanel, gbcL);

        // =====================================================
        // RIGHT PANEL
        // =====================================================

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(new Color(241, 245, 249));

        JPanel formCard = new JPanel(new GridBagLayout());

        formCard.setBackground(Color.WHITE);

        formCard.setPreferredSize(new Dimension(520, 600));

        formCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
                        ),
                        new EmptyBorder(55, 55, 55, 55)
                )
        );

        GridBagConstraints gbcR = new GridBagConstraints();

        gbcR.gridx = 0;
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.weightx = 1;

        JLabel lblWelcome = new JLabel("Welcome Back");

        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblWelcome.setForeground(new Color(15, 23, 42));

        JLabel lblText = new JLabel(
                "<html>"
                + "<div style='color:#64748B; font-size:14px;'>"
                + "Please login to continue accessing your dashboard"
                + "</div>"
                + "</html>"
        );

        // =====================================================
        // INPUT SIZE
        // =====================================================

        Dimension inputSize = new Dimension(390, 68);

        txtUsername.setPreferredSize(inputSize);

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtUsername.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT,
                "Enter your username"
        );

txtUsername.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:22;"
        + "borderWidth:1;"
        + "focusWidth:1;"
        + "innerFocusWidth:1;"
        + "focusColor:#2563EB"
);

        txtPassword.setPreferredSize(inputSize);

        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        txtPassword.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT,
                "Enter your password"
        );

txtPassword.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:22;"
        + "showRevealButton:true;"
        + "borderWidth:1;"
        + "focusColor:#2563EB"
);

        // =====================================================
        // BUTTON
        // =====================================================

        btnLogin.setPreferredSize(new Dimension(390, 72));

        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);

        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 17));

        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:22;"
                + "borderWidth:0;"
                + "focusWidth:0"
        );

        // =====================================================
        // LABELS
        // =====================================================

        JLabel lblUser = new JLabel("USERNAME");

        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(71, 85, 105));

        JLabel lblPass = new JLabel("PASSWORD");

        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(71, 85, 105));

        // =====================================================
        // ADD COMPONENTS
        // =====================================================

        gbcR.gridy = 0;
        gbcR.insets = new Insets(0, 0, 10, 0);
        formCard.add(lblWelcome, gbcR);

        gbcR.gridy = 1;
        gbcR.insets = new Insets(0, 0, 40, 0);
        formCard.add(lblText, gbcR);

        gbcR.gridy = 2;
        gbcR.insets = new Insets(0, 0, 10, 0);
        formCard.add(lblUser, gbcR);

        gbcR.gridy = 3;
        gbcR.insets = new Insets(0, 0, 28, 0);
        formCard.add(txtUsername, gbcR);

        gbcR.gridy = 4;
        gbcR.insets = new Insets(0, 0, 10, 0);
        formCard.add(lblPass, gbcR);

        gbcR.gridy = 5;
        gbcR.insets = new Insets(0, 0, 40, 0);
        formCard.add(txtPassword, gbcR);

        gbcR.gridy = 6;
        gbcR.insets = new Insets(10, 0, 0, 0);
        formCard.add(btnLogin, gbcR);

        rightWrapper.add(formCard);

        container.add(leftPanel);
        container.add(rightWrapper);

        add(container);
    }

    private JPanel createStatCard(String value, String title) {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblValue = new JLabel(value);

        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel lblTitle = new JLabel(title);

        lblTitle.setForeground(new Color(203, 213, 225));
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(lblValue);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTitle);

        return panel;
    }

    private void handleLogin() {

        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Fields required!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        btnLogin.setEnabled(false);
        btnLogin.setText("AUTHENTICATING...");

        new Thread(() -> {

            User auth = authService.login(user, pass);

            SwingUtilities.invokeLater(() -> {

                setCursor(Cursor.getDefaultCursor());

                btnLogin.setEnabled(true);
                btnLogin.setText("SIGN IN TO SYSTEM");

                if (auth != null) {

                    new DashboardForm(auth).setVisible(true);

                    this.dispose();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Login Gagal!",
                            "Access Denied",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

        }).start();
    }
}