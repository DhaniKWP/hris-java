/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package form;

import entity.User;
import entity.Employee;
import entity.AuditLog;
import service.EmployeeService;
import service.AuditService;
import service.UserService; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author macbook
 */
public class DashboardForm extends JFrame {
    private User currentUser;
    
    // Services
    private EmployeeService employeeService = new EmployeeService();
    private AuditService auditService = new AuditService();
    private UserService userService = new UserService();
    
    // State Lists
    private ArrayList<Employee> currentList = new ArrayList<>(); 
    private ArrayList<User> userList = new ArrayList<>();
    
    // UI Components - Karyawan
    private JTabbedPane tabPane = new JTabbedPane();
    private JTable tableEmployee = new JTable();
    private DefaultTableModel tableModel;
    private JButton btnAdd = new JButton("Tambah");
    private JButton btnEdit = new JButton("Edit");
    private JButton btnDelete = new JButton("Soft Delete");
    private JButton btnRefresh = new JButton("Refresh");
    private JButton btnLogout = new JButton("Logout");
    
    // UI Components - Audit
    private JTable tableAudit = new JTable();
    private DefaultTableModel auditModel;
    
    // UI Components - User
    private JTable tableUser = new JTable();
    private DefaultTableModel userModel;

    public DashboardForm(User user) {
        this.currentUser = user;
        setTitle("HRIS Dashboard - Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        applyRolePermissions();
        
        // 🔥 Load semua data pas aplikasi pertama kali dibuka
        loadData();
        loadAuditData();
        loadUserData(); 
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ==========================================
        // 🔥 HEADER: PANEL WELCOME & LOGOUT
        // ==========================================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding biar ga mepet ujung
        
        JLabel lblWelcome = new JLabel("Halo, " + currentUser.getUsername() + " | Hak Akses: " + currentUser.getRole());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(255, 69, 58)); // Opsional: Kasih warna merah dikit kalau lu pake UI manager custom
        
        pnlHeader.add(lblWelcome, BorderLayout.WEST);
        pnlHeader.add(btnLogout, BorderLayout.EAST);
        
        // Taruh header di paling atas frame
        add(pnlHeader, BorderLayout.NORTH);


        // ==========================================
        // 1. PANEL DATA KARYAWAN
        // ==========================================
        JPanel pnlEmployee = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "NIK", "Nama", "Jabatan", "Dept", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        tableEmployee.setModel(tableModel);
        
        JPanel pnlAction = new JPanel();
        pnlAction.add(btnAdd);
        pnlAction.add(btnEdit);
        pnlAction.add(btnDelete);
        pnlAction.add(btnRefresh);
        
        pnlEmployee.add(new JScrollPane(tableEmployee), BorderLayout.CENTER);
        pnlEmployee.add(pnlAction, BorderLayout.SOUTH);

        // ==========================================
        // 2. PANEL AUDIT TRAIL
        // ==========================================
        JPanel pnlAudit = new JPanel(new BorderLayout());
        auditModel = new DefaultTableModel(new Object[]{
            "Waktu", "Pelaku (User)", "Karyawan", "Aksi", "Field Diubah", "Nilai Lama", "Nilai Baru"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAudit.setModel(auditModel);
        
        JButton btnRefreshAudit = new JButton("Refresh Audit Trail");
        JPanel pnlAuditAction = new JPanel();
        pnlAuditAction.add(btnRefreshAudit);
        
        pnlAudit.add(new JScrollPane(tableAudit), BorderLayout.CENTER);
        pnlAudit.add(pnlAuditAction, BorderLayout.SOUTH);

        // ==========================================
        // 3. PANEL USER MANAGEMENT
        // ==========================================
        JPanel pnlUsers = new JPanel(new BorderLayout());
        userModel = new DefaultTableModel(new Object[]{"ID", "Username", "Password", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableUser.setModel(userModel);
        
        JPanel pnlUserAction = new JPanel();
        JButton btnAddUser = new JButton("Tambah User");
        JButton btnEditUser = new JButton("Edit User");
        JButton btnDeleteUser = new JButton("Hapus User");
        JButton btnRefreshUser = new JButton("Refresh");

        pnlUserAction.add(btnAddUser);
        pnlUserAction.add(btnEditUser);
        pnlUserAction.add(btnDeleteUser);
        pnlUserAction.add(btnRefreshUser);
        
        pnlUsers.add(new JScrollPane(tableUser), BorderLayout.CENTER);
        pnlUsers.add(pnlUserAction, BorderLayout.SOUTH);

        // ==========================================
        // MASUKKAN SEMUA PANEL KE TAB PANE
        // ==========================================
        tabPane.addTab("Data Karyawan", pnlEmployee);
        tabPane.addTab("Audit Trail", pnlAudit);
        tabPane.addTab("User Management", pnlUsers); 

        // Taruh Tab di tengah frame
        add(tabPane, BorderLayout.CENTER);

        // ==========================================
        // ACTION LISTENERS - KARYAWAN
        // ==========================================
        btnRefresh.addActionListener(e -> loadData());
        
        btnAdd.addActionListener(e -> {
            new EmployeeForm(this, null, currentUser).setVisible(true); 
            loadData(); 
            loadAuditData();
        });

        btnEdit.addActionListener(e -> {
            int row = tableEmployee.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu bro!");
                return;
            }
            Employee selectedEmp = currentList.get(row);
            new EmployeeForm(this, selectedEmp, currentUser).setVisible(true); 
            loadData();
            loadAuditData();
        });

        btnDelete.addActionListener(e -> {
            int row = tableEmployee.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!");
                return;
            }
            
            Employee selectedEmp = currentList.get(row);
            if(selectedEmp.getStatusKerja().equals("Terminated")) {
                JOptionPane.showMessageDialog(this, "Karyawan ini statusnya sudah Terminated!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau nonaktifkan (Soft Delete) karyawan " + selectedEmp.getNamaLengkap() + "?", 
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = employeeService.softDeleteEmployee(selectedEmp, currentUser.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Karyawan berhasil di-Terminated!");
                    loadData();
                    loadAuditData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data!");
                }
            }
        });

        // ==========================================
        // ACTION LISTENERS - AUDIT & USER
        // ==========================================
        btnRefreshAudit.addActionListener(e -> loadAuditData());

        btnRefreshUser.addActionListener(e -> loadUserData());

        btnAddUser.addActionListener(e -> {
            new UserForm(this, null).setVisible(true);
            loadUserData();
        });

        btnEditUser.addActionListener(e -> {
            int row = tableUser.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih akun di tabel dulu!");
                return;
            }
            new UserForm(this, userList.get(row)).setVisible(true);
            loadUserData();
        });

        btnDeleteUser.addActionListener(e -> {
            int row = tableUser.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih akun di tabel dulu!");
                return;
            }
            User selectedUser = userList.get(row);
            
            if (selectedUser.getId().equals(currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Tidak bisa menghapus akun yang sedang dipakai!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau menghapus permanen akun " + selectedUser.getUsername() + "?", 
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (userService.deleteUser(selectedUser.getId())) {
                    JOptionPane.showMessageDialog(this, "Akun berhasil dihapus!");
                    loadUserData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus akun!");
                }
            }
        });

        // ==========================================
        // 🔥 ACTION LISTENER LOGOUT
        // ==========================================
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau keluar dari aplikasi?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Menutup window Dashboard saat ini
                new LoginForm().setVisible(true); // Membuka window Login baru
            }
        });
    }

    private void applyRolePermissions() {
        String role = currentUser.getRole().toLowerCase();

        if (role.equals("hrd")) {
            tabPane.setEnabledAt(1, false); 
            tabPane.setEnabledAt(2, false); 
        } 
        else if (role.equals("auditor")) {
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
            tabPane.setEnabledAt(2, false); 
        }
    }
    
    // ==========================================
    // LOAD DATA METHODS
    // ==========================================
    private void loadAuditData() {
        auditModel.setRowCount(0);
        ArrayList<AuditLog> logs = auditService.getAuditLogs();
        for (AuditLog log : logs) {
            auditModel.addRow(new Object[]{
                log.getChangedAt(), log.getChangedByUsername(), log.getEmployeeName(),
                log.getActionType(), log.getFieldChanged(), log.getOldValue(), log.getNewValue()
            });
        }
    }
    
    private void loadUserData() {
        userModel.setRowCount(0);
        userList = userService.getUsers();
        for (User u : userList) {
            userModel.addRow(new Object[]{
                u.getId(), u.getUsername(), "********", u.getRole()
            });
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        currentList = employeeService.getEmployees();
        for (Employee e : currentList) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getNik(), e.getNamaLengkap(), 
                e.getJabatan(), e.getDepartemen(), e.getStatusKerja()
            });
        }
    }
}