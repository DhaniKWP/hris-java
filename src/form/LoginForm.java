package form;

import service.AuthService;
import entity.User;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {
    // Deklarasi tanpa instansiasi dulu agar tidak "curi start" dari tema
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AuthService authService = new AuthService();

    public LoginForm() {
        // 1. WAJIB: Aktifkan tema sebelum membuat komponen apapun
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Gagal mengaktifkan tema modern.");
        }

        setTitle("HRIS Enterprise - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        
        // 2. Inisialisasi komponen di sini
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("MASUK KE SISTEM");
        
        initModernUI();
        
        // Shortcut Enter
        this.getRootPane().setDefaultButton(btnLogin);
        btnLogin.addActionListener(e -> handleLogin());
        
        pack();
        setLocationRelativeTo(null);
    }

    private void initModernUI() {
        // Panel Utama dengan background bersih
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(45, 55, 45, 55)); // Spacing lebih lega

        // Header (Logo & Subtitle)
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblLogo = new JLabel("GLOBAL HRIS", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(new Color(41, 128, 185)); 
        
        JLabel lblSub = new JLabel("HR Management & Payroll System", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(127, 140, 141));
        
        headerPanel.add(lblLogo);
        headerPanel.add(lblSub);
        headerPanel.setBorder(new EmptyBorder(0, 0, 35, 0));

        // Center Panel (Input Fields)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // --- STYLING MODERN (FlatLaf Magic) ---
        Dimension fieldSize = new Dimension(320, 42); // Ukuran lebih tinggi agar mewah
        
        txtUsername.setPreferredSize(fieldSize);
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan Username");
        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "arc:15"); 

        txtPassword.setPreferredSize(fieldSize);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan Password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true; arc:15");

        // Input Layouting
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 0; centerPanel.add(lblUser, gbc);
        
        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 18, 0); 
        centerPanel.add(txtUsername, gbc);
        
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(lblPass, gbc);
        
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 0, 0);
        centerPanel.add(txtPassword, gbc);

        // Footer (Tombol Login)
        btnLogin.setPreferredSize(new Dimension(320, 48));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc:20"); 
        
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(35, 0, 0, 0));
        footerPanel.add(btnLogin, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);
        btnLogin.setText("MENGHUBUNGKAN...");

        new Thread(() -> {
            User authenticatedUser = authService.login(user, pass);
            
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getDefaultCursor());
                btnLogin.setEnabled(true);
                btnLogin.setText("MASUK KE SISTEM");
                
                if (authenticatedUser != null) {
                    new DashboardForm(authenticatedUser).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Kredensial salah. Silakan coba lagi.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }
}