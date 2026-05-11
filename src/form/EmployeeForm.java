package form;

import entity.Employee;
import entity.User;
import service.EmployeeService;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EmployeeForm extends JDialog {

    // --- COMPONENTS ---
    private final JTextField txtNik = new JTextField();
    private final JTextField txtNama = new JTextField();
    private final JTextField txtJabatan = new JTextField();
    private final JTextField txtDepartemen = new JTextField();
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
        "Tetap", "Kontrak", "Terminated"
    });
    private final JButton btnSave = new JButton("Simpan Perubahan");
    private final JButton btnCancel = new JButton("Batal");

    private final EmployeeService service = new EmployeeService();
    private Employee employeeToEdit = null;
    private User currentUser;

    // --- DESIGN SYSTEM (Consistent with UserForm) ---
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color PRIMARY_HOVER = new Color(30, 64, 175);
    private final Color BACKGROUND = new Color(243, 244, 246);
    private final Color TEXT_MAIN = new Color(17, 24, 39);
    private final Color TEXT_SUB = new Color(107, 114, 128);
    private final Color BORDER_COLOR = new Color(209, 213, 219);

    public EmployeeForm(JFrame parent, Employee emp, User currentUser) {
        super(parent, true);
        this.employeeToEdit = emp;
        this.currentUser = currentUser;

        setTitle(emp == null ? "Tambah Data Karyawan" : "Edit Data Karyawan");
        setSize(560, 820); // Sedikit lebih tinggi karena field lebih banyak
        setResizable(false);
        setLocationRelativeTo(parent);

        initUI();

        if (emp != null) {
            txtNik.setText(emp.getNik());
            txtNama.setText(emp.getNamaLengkap());
            txtJabatan.setText(emp.getJabatan());
            txtDepartemen.setText(emp.getDepartemen());
            cmbStatus.setSelectedItem(emp.getStatusKerja());
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);

        // --- THE PREMIUM CARD ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 45, 40, 45));
        card.setPreferredSize(new Dimension(480, 720));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 25");

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.weightx = 1.0;

        // 1. Badge
        JLabel badge = new JLabel(" EMPLOYEE MANAGEMENT SYSTEM ");
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

        // 2. Title & Subtitle
        JLabel titleLabel = new JLabel(employeeToEdit == null ? "Tambah Karyawan" : "Edit Karyawan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_MAIN);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 6, 0);
        card.add(titleLabel, g);

        JLabel subLabel = new JLabel("<html>Kelola data profil dan status kepegawaian secara akurat.</html>");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(TEXT_SUB);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 30, 0);
        card.add(subLabel, g);

        // 3. Form Fields (Spacing Konsisten)
        g.insets = new Insets(0, 0, 15, 0);
        
        g.gridy = 3;
        card.add(createStyledField("NIK (Nomor Induk Karyawan)", txtNik, "Contoh: 202401001"), g);
        
        g.gridy = 4;
        card.add(createStyledField("Nama Lengkap", txtNama, "Masukkan nama lengkap karyawan"), g);
        
        g.gridy = 5;
        card.add(createStyledField("Jabatan", txtJabatan, "Contoh: Senior Developer"), g);
        
        g.gridy = 6;
        card.add(createStyledField("Departemen", txtDepartemen, "Contoh: Teknologi Informasi"), g);
        
        g.gridy = 7;
        card.add(createStyledField("Status Kerja", cmbStatus, ""), g);

        // 4. Footer Buttons
        JPanel footer = new JPanel(new GridLayout(1, 2, 14, 0));
        footer.setOpaque(false);
        
        applyButtonStyles(btnCancel, false);
        applyButtonStyles(btnSave, true);
        
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveData());

        footer.add(btnCancel);
        footer.add(btnSave);

        g.gridy = 8;
        g.weighty = 1.0;
        g.anchor = GridBagConstraints.SOUTH;
        g.insets = new Insets(30, 0, 0, 0);
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
        String nik = txtNik.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = txtJabatan.getText().trim();
        String dept = txtDepartemen.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        // --- VALIDASI BAHASA INDONESIA ---
        if (nik.isEmpty() || nama.isEmpty() || jabatan.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Validasi Gagal: Semua kolom wajib diisi!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nik.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, 
                "Validasi Gagal: NIK harus berupa angka!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success;
        if (employeeToEdit == null) {
            Employee newEmp = new Employee("", nik, nama, jabatan, dept, status);
            success = service.addEmployee(newEmp);
        } else {
            Employee updatedEmp = new Employee(employeeToEdit.getId(), nik, nama, jabatan, dept, status);
            success = service.updateEmployeeById(employeeToEdit.getId(), updatedEmp, currentUser.getId());
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan dengan aman!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}