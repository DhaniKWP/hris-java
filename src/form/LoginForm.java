/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package form;

import service.AuthService;
import entity.User;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author macbook
 */
public class LoginForm extends JFrame {
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JButton btnLogin = new JButton("Login");
    private AuthService authService = new AuthService();

    public LoginForm() {
        setTitle("HRIS - Login System");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JPanel p1 = new JPanel(); p1.add(new JLabel("Username:")); p1.add(txtUsername);
        JPanel p2 = new JPanel(); p2.add(new JLabel("Password :")); p2.add(txtPassword);
        JPanel p3 = new JPanel(); p3.add(btnLogin);

        add(p1); add(p2); add(p3);

        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText();
            String pass = new String(txtPassword.getPassword());

            User authenticatedUser = authService.login(user, pass);

            if (authenticatedUser != null) {
                // Buka Dashboard dan oper object User-nya
                new DashboardForm(authenticatedUser).setVisible(true);
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Login Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
