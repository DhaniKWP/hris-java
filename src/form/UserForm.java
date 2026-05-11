package form;

import entity.User;
import service.UserService;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserForm extends JDialog {

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[]{
            "Super Admin", "HRD", "Auditor"
    });

    private final JButton btnSave = new JButton("Save Changes");
    private final JButton btnCancel = new JButton("Cancel");
    private final UserService service = new UserService();
    private User userToEdit;

    // --- PREMIUM DESIGN SYSTEM ---
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color PRIMARY_HOVER = new Color(30, 64, 175);
    private final Color BACKGROUND = new Color(243, 244, 246);
    private final Color TEXT_MAIN = new Color(17, 24, 39);
    private final Color TEXT_SUB = new Color(107, 114, 128);
    private final Color BORDER_COLOR = new Color(209, 213, 219);

    public UserForm(JFrame parent, User user) {
        super(parent, true);
        this.userToEdit = user;

        setTitle(user == null ? "Create New User" : "Edit User");
        setSize(520, 680); 
        setResizable(false);
        setLocationRelativeTo(parent);

        initUI();

        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());
            cmbRole.setSelectedIndex(Math.max(0, user.getRoleId() - 1));
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);

        // --- THE PREMIUM CARD ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 45, 40, 45));
        card.setPreferredSize(new Dimension(440, 580)); // Ukuran card diperpendek biar gak kosong
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 25"); 

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.weightx = 1.0;

        // 1. Badge
        JLabel badge = new JLabel(" HUMAN RESOURCE SYSTEM ");
        badge.setOpaque(true);
        badge.setBackground(new Color(239, 246, 255));
        badge.setForeground(PRIMARY);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        
        JPanel badgeAlign = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeAlign.setOpaque(false);
        badgeAlign.add(badge);
        
        g.gridy = 0;
        g.insets = new Insets(0, 0, 18, 0);
        card.add(badgeAlign, g);

        // 2. Title
        JLabel titleLabel = new JLabel(userToEdit == null ? "Create User Account" : "Edit User Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_MAIN);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 6, 0);
        card.add(titleLabel, g);

        // 3. Subtitle
        JLabel subLabel = new JLabel("<html>Manage access control and account roles efficiently.</html>");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(TEXT_SUB);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 30, 0);
        card.add(subLabel, g);

        // 4. Form Fields (Jarak vertikal diatur di Insets)
        g.gridy = 3;
        g.insets = new Insets(0, 0, 18, 0);
        card.add(createStyledField("Username", txtUsername, "Enter account username"), g);
        
        g.gridy = 4;
        g.insets = new Insets(0, 0, 18, 0);
        card.add(createStyledField("Password", txtPassword, "Enter secure password"), g);
        
        g.gridy = 5;
        g.insets = new Insets(0, 0, 0, 0);
        card.add(createStyledField("Role Access", cmbRole, ""), g);

        // 5. Footer Buttons (Diletakkan di bawah dengan weighty)
        JPanel footer = new JPanel(new GridLayout(1, 2, 14, 0));
        footer.setOpaque(false);
        
        applyButtonStyles(btnCancel, false);
        applyButtonStyles(btnSave, true);
        
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveData());

        footer.add(btnCancel);
        footer.add(btnSave);

        g.gridy = 6;
        g.weighty = 1.0; 
        g.anchor = GridBagConstraints.SOUTH; 
        g.insets = new Insets(35, 0, 0, 0); // Jarak dari role access ke tombol
        card.add(footer, g);

        root.add(card);
        setContentPane(root);
    }

    private JPanel createStyledField(String labelText, JComponent comp, String hint) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_MAIN);

        comp.setPreferredSize(new Dimension(0, 42));
        if (comp instanceof JTextField tf) {
            tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
            tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; focusWidth: 2;");
        }
        
        if (comp instanceof JComboBox cb) {
            cb.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        }

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void applyButtonStyles(JButton btn, boolean isPrimary) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        
        if (isPrimary) {
            btn.setBackground(PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0;");
            
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_HOVER); }
                public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
            });
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT_MAIN);
            btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderColor: #D1D5DB;");
            
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(249, 250, 251)); }
                public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
            });
        }
    }

    private void saveData() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi semua kolom!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User u = new User(userToEdit == null ? "" : userToEdit.getId(), user, pass, "", cmbRole.getSelectedIndex() + 1);
        if (service.saveOrUpdateUser(u)) {
            JOptionPane.showMessageDialog(this, "User berhasil disimpan!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}