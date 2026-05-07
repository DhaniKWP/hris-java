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
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    private JButton btnExportAudit = new JButton("Export to Excel (CSV)");
    
    // UI Components - User
    private JTable tableUser = new JTable();
    private DefaultTableModel userModel;
    
    // 🔥 Komponen Filter Data Karyawan
    private JTextField txtSearchEmp = new JTextField(15);
    private JComboBox<String> cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "Aktif", "Terminated"});
    private TableRowSorter<DefaultTableModel> sorterEmployee;

    // 🔥 Komponen Filter Data Audit Trail
    private JTextField txtSearchAudit = new JTextField(15);
    private JComboBox<String> cmbFilterAction = new JComboBox<>(new String[]{"Semua", "UPDATE", "DELETE"});
    private TableRowSorter<DefaultTableModel> sorterAudit;

    public DashboardForm(User user) {
        this.currentUser = user;
        setTitle("HRIS Dashboard - Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        applyRolePermissions();
        
        loadData();
        loadAuditData();
        loadUserData(); 
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ==========================================
        // HEADER: PANEL WELCOME & LOGOUT
        // ==========================================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
        
        JLabel lblWelcome = new JLabel("Halo, " + currentUser.getUsername() + " | Hak Akses: " + currentUser.getRole());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnLogout.setBackground(new Color(255, 69, 58)); 
        
        pnlHeader.add(lblWelcome, BorderLayout.WEST);
        pnlHeader.add(btnLogout, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // ==========================================
        // 1. PANEL DATA KARYAWAN + FILTER
        // ==========================================
        JPanel pnlEmployee = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "NIK", "Nama", "Jabatan", "Dept", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        tableEmployee.setModel(tableModel);
        
        sorterEmployee = new TableRowSorter<>(tableModel);
        tableEmployee.setRowSorter(sorterEmployee);

        JPanel pnlFilterEmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFilterEmp.add(new JLabel("Cari (NIK/Nama):"));
        pnlFilterEmp.add(txtSearchEmp);
        pnlFilterEmp.add(new JLabel("  Filter Status:"));
        pnlFilterEmp.add(cmbFilterStatus);
        
        JPanel pnlAction = new JPanel();
        pnlAction.add(btnAdd);
        pnlAction.add(btnEdit);
        pnlAction.add(btnDelete);
        pnlAction.add(btnRefresh);
        
        pnlEmployee.add(pnlFilterEmp, BorderLayout.NORTH); 
        pnlEmployee.add(new JScrollPane(tableEmployee), BorderLayout.CENTER);
        pnlEmployee.add(pnlAction, BorderLayout.SOUTH);

        // ==========================================
        // 2. PANEL AUDIT TRAIL + FILTER 🔥
        // ==========================================
        JPanel pnlAudit = new JPanel(new BorderLayout());
        auditModel = new DefaultTableModel(new Object[]{
            "Waktu", "Pelaku (User)", "Karyawan", "Aksi", "Field Diubah", "Nilai Lama", "Nilai Baru"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAudit.setModel(auditModel);
        
        // Setup Sorter Audit
        sorterAudit = new TableRowSorter<>(auditModel);
        tableAudit.setRowSorter(sorterAudit);

        // Panel Search & Filter Audit
        JPanel pnlFilterAudit = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFilterAudit.add(new JLabel("Cari (Pelaku/Karyawan):"));
        pnlFilterAudit.add(txtSearchAudit);
        pnlFilterAudit.add(new JLabel("  Filter Aksi:"));
        pnlFilterAudit.add(cmbFilterAction);
        
        JButton btnRefreshAudit = new JButton("Refresh Audit Trail");
        btnExportAudit.setBackground(new Color(46, 204, 113));
        
        
        JPanel pnlAuditAction = new JPanel();
        pnlAuditAction.add(btnRefreshAudit);
        pnlAuditAction.add(btnExportAudit);
        
        pnlAudit.add(pnlFilterAudit, BorderLayout.NORTH); // Masukin filter ke atas
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
        add(tabPane, BorderLayout.CENTER);

        // ==========================================
        // ACTION LISTENERS - FILTER KARYAWAN
        // ==========================================
        txtSearchEmp.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyEmployeeFilter(); }
            public void removeUpdate(DocumentEvent e) { applyEmployeeFilter(); }
            public void changedUpdate(DocumentEvent e) { applyEmployeeFilter(); }
        });
        cmbFilterStatus.addActionListener(e -> applyEmployeeFilter());

        // ==========================================
        // 🔥 ACTION LISTENERS - FILTER AUDIT
        // ==========================================
        txtSearchAudit.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyAuditFilter(); }
            public void removeUpdate(DocumentEvent e) { applyAuditFilter(); }
            public void changedUpdate(DocumentEvent e) { applyAuditFilter(); }
        });
        cmbFilterAction.addActionListener(e -> applyAuditFilter());


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
            int modelRow = tableEmployee.convertRowIndexToModel(row);
            Employee selectedEmp = currentList.get(modelRow);
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
            
            int modelRow = tableEmployee.convertRowIndexToModel(row);
            Employee selectedEmp = currentList.get(modelRow);
            
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
        btnExportAudit.addActionListener(e -> exportAuditToCSV());

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
        // ACTION LISTENER LOGOUT
        // ==========================================
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau keluar dari aplikasi?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); 
                new LoginForm().setVisible(true); 
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
    // METHOD FILTER KARYAWAN
    // ==========================================
    private void applyEmployeeFilter() {
        String searchTxt = txtSearchEmp.getText().toLowerCase();
        String status = cmbFilterStatus.getSelectedItem().toString();

        List<RowFilter<Object,Object>> filters = new ArrayList<>();

        if (searchTxt.trim().length() > 0) {
            filters.add(RowFilter.regexFilter("(?i)" + searchTxt, 1, 2)); // Index 1: NIK, 2: Nama
        }

        if (status.equals("Aktif")) {
            filters.add(RowFilter.regexFilter("(?i)Tetap|Kontrak", 5)); // Index 5: Status
        } else if (status.equals("Terminated")) {
            filters.add(RowFilter.regexFilter("(?i)Terminated", 5));
        }

        sorterEmployee.setRowFilter(RowFilter.andFilter(filters));
    }

    // ==========================================
    // 🔥 METHOD FILTER AUDIT
    // ==========================================
    private void applyAuditFilter() {
        String searchTxt = txtSearchAudit.getText().toLowerCase();
        String action = cmbFilterAction.getSelectedItem().toString();

        List<RowFilter<Object,Object>> filters = new ArrayList<>();

        // Cari di Index 1 (Pelaku/User) dan Index 2 (Nama Karyawan)
        if (searchTxt.trim().length() > 0) {
            filters.add(RowFilter.regexFilter("(?i)" + searchTxt, 1, 2)); 
        }

        // Filter berdasarkan aksi (Index 3)
        if (!action.equals("Semua")) {
            filters.add(RowFilter.regexFilter("(?i)" + action, 3)); 
        }

        sorterAudit.setRowFilter(RowFilter.andFilter(filters));
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
        // 🔥 Terapkan ulang filter setiap kali data direfresh
        applyAuditFilter();
    }
    
    // ==========================================
    // 🔥 METHOD EXPORT AUDIT KE EXCEL (CSV)
    // ==========================================
    private void exportAuditToCSV() {
        // Bikin dialog buat milih lokasi save file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Lokasi Simpan Laporan Audit");
        fileChooser.setSelectedFile(new java.io.File("Laporan_Audit_Trail_HRIS.csv")); // Nama default

        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                // 1. Tulis Header (Judul Kolom)
                writer.println("Waktu,Pelaku (User),Karyawan,Aksi,Field Diubah,Nilai Lama,Nilai Baru");

                // 2. Looping nulis isi data dari JTable
                for (int i = 0; i < auditModel.getRowCount(); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < auditModel.getColumnCount(); j++) {
                        String value = auditModel.getValueAt(i, j) != null ? auditModel.getValueAt(i, j).toString() : "";
                        
                        // Trik Penting: Ganti koma di dalam teks jadi titik koma, biar format CSV ga berantakan
                        value = value.replace(",", ";"); 
                        
                        sb.append(value);
                        if (j < auditModel.getColumnCount() - 1) {
                            sb.append(","); // Pemisah antar kolom
                        }
                    }
                    writer.println(sb.toString()); // Pindah baris baru
                }
                
                JOptionPane.showMessageDialog(this, "Sukses export data!\nFile tersimpan di:\n" + fileToSave.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal export data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        applyEmployeeFilter(); 
    }
}