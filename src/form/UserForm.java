/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package form;

import entity.User;
import service.UserService;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author macbook
 */
public class UserForm extends JDialog {
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    // Berdasarkan database lu: 1=superadmin, 2=HRD, 3=auditor
    private JComboBox<String> cmbRole = new JComboBox<>(new String[]{"1 - superadmin", "2 - HRD", "3 - auditor"});
    private JButton btnSave = new JButton("Simpan Akun");
    
    private UserService service = new UserService();
    private User userToEdit = null;

    public UserForm(JFrame parent, User u) {
        super(parent, u == null ? "Tambah User Baru" : "Edit User", true);
        this.userToEdit = u;
        
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel(" Username:")); add(txtUsername);
        add(new JLabel(" Password:")); add(txtPassword);
        add(new JLabel(" Hak Akses (Role):")); add(cmbRole);
        add(new JLabel("")); add(btnSave);

        if (u != null) {
            txtUsername.setText(u.getUsername());
            txtPassword.setText(u.getPassword());
            cmbRole.setSelectedIndex(u.getRoleId() - 1); // Index array mulai dari 0
        }

        btnSave.addActionListener(e -> saveData());
    }

    private void saveData() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        int roleId = cmbRole.getSelectedIndex() + 1; // +1 karena index 0 = role_id 1

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!");
            return;
        }

        String id = (userToEdit == null) ? "" : userToEdit.getId();
        User mappedUser = new User(id, username, password, "", roleId);

        if (service.saveOrUpdateUser(mappedUser)) {
            JOptionPane.showMessageDialog(this, "Akun berhasil disimpan!");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan akun!");
        }
    }
}
