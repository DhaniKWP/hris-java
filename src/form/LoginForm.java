package form;

import service.AuthService;
import entity.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JButton btnLogin = new JButton("MASUK KE SISTEM");
    private AuthService authService = new AuthService();

    public LoginForm() {
        setTitle("HRIS Enterprise - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        
        // --- UI CUSTOMIZATION ---
        initModernUI();
        
        // Action Listener
        btnLogin.addActionListener(e -> handleLogin());
        
        pack();
        setLocationRelativeTo(null);
    }

    private void initModernUI() {
        // 1. Panel Utama dengan Background Putih
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 2. Bagian Atas (Logo & Judul)
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblLogo = new JLabel("GLOBAL HRIS", SwingConstants.CENTER);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblLogo.setForeground(new Color(41, 128, 185)); // Biru Corporate
        
        JLabel lblSub = new JLabel("Silakan masuk dengan akun Anda", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        
        headerPanel.add(lblLogo);
        headerPanel.add(lblSub);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // 3. Bagian Tengah (Input Fields)
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        // Styling TextField
        Dimension fieldSize = new Dimension(280, 35);
        txtUsername.setPreferredSize(fieldSize);
        txtPassword.setPreferredSize(fieldSize);

        centerPanel.add(new JLabel("Username"));
        centerPanel.add(txtUsername);
        centerPanel.add(new JLabel("Password"));
        centerPanel.add(txtPassword);

        // 4. Bagian Bawah (Tombol)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnLogin.setPreferredSize(new Dimension(280, 40));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(btnLogin, BorderLayout.CENTER);

        // Gabungkan ke Main Panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Efek Loading: Ubah kursor jadi berputar
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);

        // Gunakan Thread agar UI tidak freeze saat cek ke Supabase
        new Thread(() -> {
            User authenticatedUser = authService.login(user, pass);
            
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getDefaultCursor());
                btnLogin.setEnabled(true);
                
                if (authenticatedUser != null) {
                    new DashboardForm(authenticatedUser).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Login Gagal! Akun tidak ditemukan.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }
}