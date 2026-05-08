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
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

/**
 * HRIS Dashboard - Professional Revamp with Grid & Centered ID
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
    
    // Filter Components
    private JTextField txtSearchEmp = new JTextField(15);
    private JComboBox<String> cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "Aktif", "Terminated"});
    private TableRowSorter<DefaultTableModel> sorterEmployee;
    private JTextField txtSearchAudit = new JTextField(15);
    private JComboBox<String> cmbFilterAction = new JComboBox<>(new String[]{"Semua", "UPDATE", "DELETE"});
    private TableRowSorter<DefaultTableModel> sorterAudit;
    // Filter tanggal
    private JDateChooser dateAuditStart = new JDateChooser();
    private JDateChooser dateAuditEnd = new JDateChooser();

    // Corporate Colors
    private Color primaryBlue = new Color(41, 128, 185);
    private Color darkHeader = new Color(44, 62, 80);
    private Color successGreen = new Color(46, 204, 113);
    private Color dangerRed = new Color(231, 76, 60);

    public DashboardForm(User user) {
        this.currentUser = user;
        setTitle("GLOBAL HRIS - Professional Suite");
        setSize(1150, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        applyRolePermissions();
        
        loadData();
        loadAuditData();
        loadUserData(); 
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setGridColor(new Color(210, 210, 210)); // Warna garis dipertegas sedikit
        table.setShowGrid(true); // 🔥 MENAMPILKAN GARIS (GRID)
        table.setIntercellSpacing(new Dimension(1, 1)); // Jarak antar cell untuk garis
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(41, 128, 185, 40));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(100, 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));

        // 🔥 SETTING TEKS TENGAH (CENTER ALIGNMENT) UNTUK KOLOM PERTAMA (ID)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // Jika di Tabel Karyawan kolom NIK (indeks 1) mau tengah juga, tambahkan ini:
        if(table.getColumnCount() > 1) {
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(darkHeader);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); 
        
        JLabel lblWelcome = new JLabel("<html><font color='white'>Halo, <b>" + currentUser.getUsername() + "</b></font> <font color='#bdc3c7'>| " + currentUser.getRole() + "</font></html>");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        styleButton(btnLogout, dangerRed, Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(100, 30));
        
        pnlHeader.add(lblWelcome, BorderLayout.WEST);
        pnlHeader.add(btnLogout, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // 1. PANEL DATA KARYAWAN
        JPanel pnlEmployee = new JPanel(new BorderLayout());
        pnlEmployee.setBackground(Color.WHITE);
        pnlEmployee.setBorder(new EmptyBorder(15, 15, 15, 15));

        tableModel = new DefaultTableModel(new Object[]{"ID", "NIK", "Nama", "Jabatan", "Dept", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; } 
        };
        tableEmployee.setModel(tableModel);
        sorterEmployee = new TableRowSorter<>(tableModel);
        tableEmployee.setRowSorter(sorterEmployee);

        JPanel pnlFilterEmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlFilterEmp.setBackground(new Color(248, 249, 250));
        pnlFilterEmp.add(new JLabel("Cari NIK/Nama:"));
        pnlFilterEmp.add(txtSearchEmp);
        pnlFilterEmp.add(new JLabel("  Status:"));
        pnlFilterEmp.add(cmbFilterStatus);
        
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlAction.setBackground(Color.WHITE);
        styleButton(btnAdd, successGreen, Color.WHITE);
        styleButton(btnEdit, primaryBlue, Color.WHITE);
        styleButton(btnDelete, new Color(230, 126, 34), Color.WHITE);
        styleButton(btnRefresh, Color.GRAY, Color.WHITE);
        
        pnlAction.add(btnRefresh); pnlAction.add(btnDelete); pnlAction.add(btnEdit); pnlAction.add(btnAdd);
        
        pnlEmployee.add(pnlFilterEmp, BorderLayout.NORTH); 
        pnlEmployee.add(new JScrollPane(tableEmployee), BorderLayout.CENTER);
        pnlEmployee.add(pnlAction, BorderLayout.SOUTH);

        // 2. PANEL AUDIT TRAIL
        JPanel pnlAudit = new JPanel(new BorderLayout());
        pnlAudit.setBackground(Color.WHITE);
        pnlAudit.setBorder(new EmptyBorder(15, 15, 15, 15));

        auditModel = new DefaultTableModel(new Object[]{"Waktu", "User", "Karyawan", "Aksi", "Field", "Lama", "Baru"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAudit.setModel(auditModel);
        sorterAudit = new TableRowSorter<>(auditModel);
        tableAudit.setRowSorter(sorterAudit);

        JPanel pnlFilterAudit = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlFilterAudit.setBackground(new Color(248, 249, 250));
        pnlFilterAudit.add(new JLabel("Cari Audit:"));
        pnlFilterAudit.add(txtSearchAudit);
        pnlFilterAudit.add(new JLabel("  Filter Aksi:"));
        pnlFilterAudit.add(cmbFilterAction);
        
        dateAuditStart.setDateFormatString("yyyy-MM-dd");
        dateAuditEnd.setDateFormatString("yyyy-MM-dd");
        dateAuditStart.setPreferredSize(new Dimension(130, 30));
        dateAuditEnd.setPreferredSize(new Dimension(130, 30));

        pnlFilterAudit.add(new JLabel("  Dari Tgl:"));
        pnlFilterAudit.add(dateAuditStart); // Masukin kalender 1
        pnlFilterAudit.add(new JLabel("  Sampai:"));
        pnlFilterAudit.add(dateAuditEnd); // Masukin kalender 2
        
        JButton btnRefreshAudit = new JButton("Refresh Audit");
        styleButton(btnRefreshAudit, Color.GRAY, Color.WHITE);
        styleButton(btnExportAudit, successGreen, Color.WHITE);
        
        JPanel pnlAuditAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlAuditAction.setBackground(Color.WHITE);
        pnlAuditAction.add(btnRefreshAudit);
        pnlAuditAction.add(btnExportAudit);
        
        pnlAudit.add(pnlFilterAudit, BorderLayout.NORTH);
        pnlAudit.add(new JScrollPane(tableAudit), BorderLayout.CENTER);
        pnlAudit.add(pnlAuditAction, BorderLayout.SOUTH);

        // 3. PANEL USER MANAGEMENT
        JPanel pnlUsers = new JPanel(new BorderLayout());
        pnlUsers.setBackground(Color.WHITE);
        pnlUsers.setBorder(new EmptyBorder(15, 15, 15, 15));

        userModel = new DefaultTableModel(new Object[]{"ID", "Username", "Password", "Role"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableUser.setModel(userModel);
        
        JPanel pnlUserAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlUserAction.setBackground(Color.WHITE);
        JButton btnAddUser = new JButton("Tambah User");
        JButton btnEditUser = new JButton("Edit User");
        JButton btnDeleteUser = new JButton("Hapus User");
        JButton btnRefreshUser = new JButton("Refresh");
        
        styleButton(btnAddUser, primaryBlue, Color.WHITE);
        styleButton(btnEditUser, primaryBlue, Color.WHITE);
        styleButton(btnDeleteUser, dangerRed, Color.WHITE);
        styleButton(btnRefreshUser, Color.GRAY, Color.WHITE);

        pnlUserAction.add(btnRefreshUser); pnlUserAction.add(btnDeleteUser);
        pnlUserAction.add(btnEditUser); pnlUserAction.add(btnAddUser);
        
        pnlUsers.add(new JScrollPane(tableUser), BorderLayout.CENTER);
        pnlUsers.add(pnlUserAction, BorderLayout.SOUTH);

        // 🔥 APPLY STYLING TO ALL TABLES
        styleTable(tableEmployee);
        styleTable(tableAudit);
        styleTable(tableUser);
        
        tableEmployee.removeColumn(tableEmployee.getColumnModel().getColumn(0));
        tableUser.removeColumn(tableUser.getColumnModel().getColumn(0));

        tabPane.addTab(" Database Karyawan ", pnlEmployee);
        tabPane.addTab(" Audit Trail System ", pnlAudit);
        tabPane.addTab(" User Management ", pnlUsers); 
        add(tabPane, BorderLayout.CENTER);

        // ACTION LISTENERS
        txtSearchEmp.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyEmployeeFilter(); }
            public void removeUpdate(DocumentEvent e) { applyEmployeeFilter(); }
            public void changedUpdate(DocumentEvent e) { applyEmployeeFilter(); }
        });
        cmbFilterStatus.addActionListener(e -> applyEmployeeFilter());

        txtSearchAudit.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyAuditFilter(); }
            public void removeUpdate(DocumentEvent e) { applyAuditFilter(); }
            public void changedUpdate(DocumentEvent e) { applyAuditFilter(); }
        });
        cmbFilterAction.addActionListener(e -> applyAuditFilter());
        
        java.beans.PropertyChangeListener dateListener = evt -> {
            if ("date".equals(evt.getPropertyName())) {
                applyAuditFilter();
            }
        };
        dateAuditStart.addPropertyChangeListener(dateListener);
        dateAuditEnd.addPropertyChangeListener(dateListener);

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> { new EmployeeForm(this, null, currentUser).setVisible(true); loadData(); loadAuditData(); });
        btnEdit.addActionListener(e -> {
            int row = tableEmployee.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!"); return; }
            int modelRow = tableEmployee.convertRowIndexToModel(row);
            Employee selectedEmp = currentList.get(modelRow);
            new EmployeeForm(this, selectedEmp, currentUser).setVisible(true); 
            loadData(); loadAuditData();
        });
        btnDelete.addActionListener(e -> {
            int row = tableEmployee.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!"); return; }
            int modelRow = tableEmployee.convertRowIndexToModel(row);
            Employee selectedEmp = currentList.get(modelRow);
            if (JOptionPane.showConfirmDialog(this, "Nonaktifkan karyawan " + selectedEmp.getNamaLengkap() + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (employeeService.softDeleteEmployee(selectedEmp, currentUser.getId())) {
                    loadData(); loadAuditData();
                }
            }
        });

        btnRefreshAudit.addActionListener(e -> loadAuditData());
        btnRefreshUser.addActionListener(e -> loadUserData());
        btnExportAudit.addActionListener(e -> exportAuditToCSV());
        btnAddUser.addActionListener(e -> { new UserForm(this, null).setVisible(true); loadUserData(); });
        btnEditUser.addActionListener(e -> {
            int row = tableUser.getSelectedRow();
            if (row != -1) { new UserForm(this, userList.get(row)).setVisible(true); loadUserData(); }
        });
        btnDeleteUser.addActionListener(e -> {
            int row = tableUser.getSelectedRow();
            if (row != -1) {
                User selectedUser = userList.get(row);
                if (!selectedUser.getId().equals(currentUser.getId())) {
                    if (userService.deleteUser(selectedUser.getId())) loadUserData();
                }
            }
        });
        btnLogout.addActionListener(e -> {
            this.dispose(); new LoginForm().setVisible(true); 
        });
    }

    private void applyRolePermissions() {
        String role = currentUser.getRole().toLowerCase();
        if (role.equals("hrd")) { tabPane.setEnabledAt(1, false); tabPane.setEnabledAt(2, false); }
        else if (role.equals("auditor")) { btnAdd.setEnabled(false); btnEdit.setEnabled(false); btnDelete.setEnabled(false); tabPane.setEnabledAt(2, false); }
    }
    
    private void applyEmployeeFilter() {
        String searchTxt = txtSearchEmp.getText().toLowerCase();
        String status = cmbFilterStatus.getSelectedItem().toString();
        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        if (searchTxt.trim().length() > 0) filters.add(RowFilter.regexFilter("(?i)" + searchTxt, 1, 2));
        if (status.equals("Aktif")) filters.add(RowFilter.regexFilter("(?i)Tetap|Kontrak", 5));
        else if (status.equals("Terminated")) filters.add(RowFilter.regexFilter("(?i)Terminated", 5));
        sorterEmployee.setRowFilter(RowFilter.andFilter(filters));
    }

    private void applyAuditFilter() {
        String searchTxt = txtSearchAudit.getText().toLowerCase();
        String action = cmbFilterAction.getSelectedItem().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = dateAuditStart.getDate() != null ? sdf.format(dateAuditStart.getDate()) : "";
        String endDate = dateAuditEnd.getDate() != null ? sdf.format(dateAuditEnd.getDate()) : "";

        List<RowFilter<Object,Object>> filters = new ArrayList<>();

        if (searchTxt.trim().length() > 0) filters.add(RowFilter.regexFilter("(?i)" + searchTxt, 1, 2)); 
        if (!action.equals("Semua")) filters.add(RowFilter.regexFilter("(?i)" + action, 3)); 

        // 🔥 LOGIC FILTER TANGGAL AUDIT
        if (!startDate.isEmpty() || !endDate.isEmpty()) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    // Kolom Waktu ada di index 0
                    String rowDate = entry.getStringValue(0); 
                    if (rowDate == null || rowDate.length() < 10) return false;
                    
                    // Ambil format YYYY-MM-DD aja dari database
                    String dateOnly = rowDate.substring(0, 10);
                    
                    boolean matches = true;
                    // Membandingkan urutan alfabet/angka (Lexicographical)
                    if (!startDate.isEmpty()) matches &= (dateOnly.compareTo(startDate) >= 0);
                    if (!endDate.isEmpty()) matches &= (dateOnly.compareTo(endDate) <= 0);
                    
                    return matches;
                }
            });
        }

        sorterAudit.setRowFilter(RowFilter.andFilter(filters));
    }
    
    private void loadAuditData() {
        auditModel.setRowCount(0);
        ArrayList<AuditLog> logs = auditService.getAuditLogs();
        for (AuditLog log : logs) {
            auditModel.addRow(new Object[]{log.getChangedAt(), log.getChangedByUsername(), log.getEmployeeName(), log.getActionType(), log.getFieldChanged(), log.getOldValue(), log.getNewValue()});
        }
        applyAuditFilter();
    }
    
    private void exportAuditToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Audit Trail");

        // 🔥 1. BIKIN NAMA FILE OTOMATIS (Format: Laporan_Audit_20260508_2100.csv)
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmm").format(new java.util.Date());
        String defaultName = "Laporan_Audit_" + timeStamp + ".csv";
        fileChooser.setSelectedFile(new java.io.File(defaultName));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // 🔥 2. VALIDASI EKSTENSI: Tambahin .csv kalau user hapus ekstensinya pas ganti nama
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(path + ".csv");
            }

            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                // Tulis Header
                writer.println("Waktu,Pelaku,Karyawan,Aksi,Field,Lama,Baru");

                // 🔥 3. EXPORT DATA YANG LAGI DI-FILTER AJA
                // Pake tableAudit.getRowCount() biar yang ke-export cuma hasil pencarian/filter tanggal lu
                for (int i = 0; i < tableAudit.getRowCount(); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < tableAudit.getColumnCount(); j++) {
                        Object cellVal = tableAudit.getValueAt(i, j);
                        String value = (cellVal != null) ? cellVal.toString().replace(",", ";") : "-";
                        sb.append(value);
                        if (j < tableAudit.getColumnCount() - 1) sb.append(",");
                    }
                    writer.println(sb.toString());
                }
                
                JOptionPane.showMessageDialog(this, "Laporan berhasil diexport!\nFile: " + fileToSave.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saat export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadUserData() {
        userModel.setRowCount(0);
        userList = userService.getUsers();
        for (User u : userList) {
            userModel.addRow(new Object[]{u.getId(), u.getUsername(), "********", u.getRole()});
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        currentList = employeeService.getEmployees();
        for (Employee e : currentList) {
            tableModel.addRow(new Object[]{e.getId(), e.getNik(), e.getNamaLengkap(), e.getJabatan(), e.getDepartemen(), e.getStatusKerja()});
        }
        applyEmployeeFilter(); 
    }
}