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
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

/**
 * HRIS Dashboard - Professional Suite
 * Detail: Micro-interactions & Enhanced Visual Depth
 * @author Dias Mayri - Global Institute
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
    private JLabel lblCountEmp = new JLabel("Menampilkan: 0 data");
    
    // UI Components - Audit
    private JTable tableAudit = new JTable();
    private DefaultTableModel auditModel;
    private JButton btnExportAudit = new JButton("Export to Excel (CSV)");
    private JLabel lblCountAudit = new JLabel("Menampilkan: 0 log");
    
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

    // Corporate Colors & Accents
    private Color primaryBlue = new Color(41, 128, 185);
    private Color darkHeader = new Color(44, 62, 80);
    private Color successGreen = new Color(46, 204, 113);
    private Color dangerRed = new Color(231, 76, 60);
    private Color bgSoft = new Color(245, 247, 250);
    private Color borderColor = new Color(218, 224, 230);

    public DashboardForm(User user) {
        this.currentUser = user;
        setTitle("GLOBAL HRIS - Professional Suite v2.0");
        setSize(1200, 750); // Dikit lebih lebar biar lega
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        applyRolePermissions();
        
        loadData();
        loadAuditData();
        loadUserData(); 
    }

private void styleTable(JTable table) {
    table.setRowHeight(40);
    
    // --- PENAMBAHAN GARIS KOLOM (Garis Merah di gambar lu) ---
    table.setShowGrid(true); 
    table.setGridColor(new Color(230, 235, 240)); // Warna abu sangat muda agar tetap modern
    table.setIntercellSpacing(new Dimension(1, 1)); // Memberi jarak antar cell untuk garis
    
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.setSelectionBackground(new Color(41, 128, 185, 30));
    table.setSelectionForeground(Color.BLACK);
    
    JTableHeader header = table.getTableHeader();
    header.setPreferredSize(new Dimension(100, 45));
    header.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    header.setBackground(Color.WHITE);
    // Header juga dikasih garis vertikal biar sejajar sama kolom bawahnya
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(220, 225, 230)));

    DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            
            // Alignment logic tetap sama
            if (c == 0 || c == 1 || t.getColumnName(c).equals("Aksi") || t.getColumnName(c).equals("Status")) {
                setHorizontalAlignment(JLabel.CENTER);
            } else {
                setHorizontalAlignment(JLabel.LEADING);
                ((JLabel)comp).setBorder(new EmptyBorder(0, 10, 0, 10)); 
            }

            if (!s) {
                comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(252, 253, 255));
            }
            
            // Logic warna status/aksi tetap dipertahankan
            return comp;
        }
    };
    
    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
    }
}

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setBorder(new LineBorder(bg.darker(), 1, true));

        // Hover Effect Detail
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgSoft);
        
        

        // --- HEADER SECTION ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(darkHeader);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); 
        
        JLabel lblWelcome = new JLabel("<html><font color='#ecf0f1'>Selamat Datang,</font> <font color='white'><b>" + currentUser.getUsername() + "</b></font></html>");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        styleButton(btnLogout, dangerRed, Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(100, 32));
        
        pnlHeader.add(lblWelcome, BorderLayout.WEST);
        pnlHeader.add(btnLogout, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        tabPane.setBackground(bgSoft);
        tabPane.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));

        // --- 1. PANEL DATA KARYAWAN ---
        JPanel pnlEmployee = new JPanel(new BorderLayout());
        pnlEmployee.setBackground(bgSoft);
        pnlEmployee.setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new Object[]{"ID", "NIK", "Nama", "Jabatan", "Dept", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; } 
        };
        tableEmployee.setModel(tableModel);
        sorterEmployee = new TableRowSorter<>(tableModel);
        tableEmployee.setRowSorter(sorterEmployee);

        // Filter Bar with Shadow-like Border
        JPanel pnlFilterEmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pnlFilterEmp.setBackground(Color.WHITE);
        pnlFilterEmp.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1), new EmptyBorder(5, 5, 5, 5)));
        
        pnlFilterEmp.add(new JLabel("Cari:"));
        txtSearchEmp.setPreferredSize(new Dimension(200, 30));
        pnlFilterEmp.add(txtSearchEmp);
        pnlFilterEmp.add(new JLabel("  Status:"));
        pnlFilterEmp.add(cmbFilterStatus);
        
        // Footer Bar (Counter & Actions)
        JPanel pnlEmpFooter = new JPanel(new BorderLayout());
        pnlEmpFooter.setBackground(bgSoft);
        pnlEmpFooter.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblCountEmp.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlEmpFooter.add(lblCountEmp, BorderLayout.WEST);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setOpaque(false);
        styleButton(btnAdd, successGreen, Color.WHITE);
        styleButton(btnEdit, primaryBlue, Color.WHITE);
        styleButton(btnDelete, new Color(230, 126, 34), Color.WHITE);
        styleButton(btnRefresh, new Color(127, 140, 141), Color.WHITE);
        
        pnlAction.add(btnRefresh); pnlAction.add(btnDelete); pnlAction.add(btnEdit); pnlAction.add(btnAdd);
        pnlEmpFooter.add(pnlAction, BorderLayout.EAST);
        
        JScrollPane scrollEmp = new JScrollPane(tableEmployee);
        scrollEmp.setBorder(new LineBorder(borderColor));
        scrollEmp.getViewport().setBackground(Color.WHITE);

        pnlEmployee.add(pnlFilterEmp, BorderLayout.NORTH); 
        pnlEmployee.add(scrollEmp, BorderLayout.CENTER);
        pnlEmployee.add(pnlEmpFooter, BorderLayout.SOUTH);

        // --- 2. PANEL AUDIT TRAIL ---
        JPanel pnlAudit = new JPanel(new BorderLayout());
        pnlAudit.setBackground(bgSoft);
        pnlAudit.setBorder(new EmptyBorder(20, 20, 20, 20));

        auditModel = new DefaultTableModel(new Object[]{"Waktu", "User", "Karyawan", "Aksi", "Field", "Lama", "Baru"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAudit.setModel(auditModel);
        sorterAudit = new TableRowSorter<>(auditModel);
        tableAudit.setRowSorter(sorterAudit);

        JPanel pnlFilterAudit = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        pnlFilterAudit.setBackground(Color.WHITE);
        pnlFilterAudit.setBorder(new LineBorder(borderColor));
        
        pnlFilterAudit.add(new JLabel("Cari:"));
        pnlFilterAudit.add(txtSearchAudit);
        pnlFilterAudit.add(new JLabel(" Aksi:"));
        pnlFilterAudit.add(cmbFilterAction);
        
        dateAuditStart.setDateFormatString("yyyy-MM-dd");
        dateAuditEnd.setDateFormatString("yyyy-MM-dd");
        dateAuditStart.setPreferredSize(new Dimension(130, 28));
        dateAuditEnd.setPreferredSize(new Dimension(130, 28));

        pnlFilterAudit.add(new JLabel(" Periode:"));
        pnlFilterAudit.add(dateAuditStart); 
        pnlFilterAudit.add(new JLabel("-"));
        pnlFilterAudit.add(dateAuditEnd); 
        
        JButton btnRefreshAudit = new JButton("Refresh Audit");
        styleButton(btnRefreshAudit, new Color(127, 140, 141), Color.WHITE);
        styleButton(btnExportAudit, successGreen, Color.WHITE);
        
        JPanel pnlAuditFooter = new JPanel(new BorderLayout());
        pnlAuditFooter.setOpaque(false);
        pnlAuditFooter.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        lblCountAudit.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlAuditFooter.add(lblCountAudit, BorderLayout.WEST);

        JPanel pnlAuditAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAuditAction.setOpaque(false);
        pnlAuditAction.add(btnRefreshAudit);
        pnlAuditAction.add(btnExportAudit);
        pnlAuditFooter.add(pnlAuditAction, BorderLayout.EAST);
        
        JScrollPane scrollAudit = new JScrollPane(tableAudit);
        scrollAudit.setBorder(new LineBorder(borderColor));

        pnlAudit.add(pnlFilterAudit, BorderLayout.NORTH);
        pnlAudit.add(scrollAudit, BorderLayout.CENTER);
        pnlAudit.add(pnlAuditFooter, BorderLayout.SOUTH);

        // --- 3. PANEL USER MANAGEMENT ---
        JPanel pnlUsers = new JPanel(new BorderLayout());
        pnlUsers.setBackground(bgSoft);
        pnlUsers.setBorder(new EmptyBorder(20, 20, 20, 20));

        userModel = new DefaultTableModel(new Object[]{"ID", "Username", "Password", "Role"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableUser.setModel(userModel);
        
        JPanel pnlUserAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlUserAction.setOpaque(false);
        JButton btnAddUser = new JButton("Tambah User");
        JButton btnEditUser = new JButton("Edit User");
        JButton btnDeleteUser = new JButton("Hapus User");
        JButton btnRefreshUser = new JButton("Refresh");
        
        styleButton(btnAddUser, primaryBlue, Color.WHITE);
        styleButton(btnEditUser, primaryBlue, Color.WHITE);
        styleButton(btnDeleteUser, dangerRed, Color.WHITE);
        styleButton(btnRefreshUser, new Color(127, 140, 141), Color.WHITE);

        pnlUserAction.add(btnRefreshUser); pnlUserAction.add(btnDeleteUser);
        pnlUserAction.add(btnEditUser); pnlUserAction.add(btnAddUser);
        
        JScrollPane scrollUser = new JScrollPane(tableUser);
        scrollUser.setBorder(new LineBorder(borderColor));
        
        pnlUsers.add(scrollUser, BorderLayout.CENTER);
        pnlUsers.add(pnlUserAction, BorderLayout.SOUTH);

        // --- FINAL STYLING ---
        styleTable(tableEmployee);
        styleTable(tableAudit);
        styleTable(tableUser);
        
        tableEmployee.removeColumn(tableEmployee.getColumnModel().getColumn(0));
        tableUser.removeColumn(tableUser.getColumnModel().getColumn(0));

        tabPane.addTab(" Database Karyawan ", pnlEmployee);
        tabPane.addTab(" Audit Trail System ", pnlAudit);
        tabPane.addTab(" User Management ", pnlUsers); 
        add(tabPane, BorderLayout.CENTER);

        // --- ALL LISTENERS ---
        setupEventListeners(btnAdd, btnEdit, btnDelete, btnRefresh, btnRefreshAudit, btnRefreshUser, btnExportAudit, btnAddUser, btnEditUser, btnDeleteUser);
    }

    private void setupEventListeners(JButton... btns) {
        // Search Karyawan
        txtSearchEmp.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyEmployeeFilter(); }
            public void removeUpdate(DocumentEvent e) { applyEmployeeFilter(); }
            public void changedUpdate(DocumentEvent e) { applyEmployeeFilter(); }
        });
        cmbFilterStatus.addActionListener(e -> applyEmployeeFilter());

        // Search Audit
        txtSearchAudit.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyAuditFilter(); }
            public void removeUpdate(DocumentEvent e) { applyAuditFilter(); }
            public void changedUpdate(DocumentEvent e) { applyAuditFilter(); }
        });
        cmbFilterAction.addActionListener(e -> applyAuditFilter());
        
        java.beans.PropertyChangeListener dateListener = evt -> {
            if ("date".equals(evt.getPropertyName())) { applyAuditFilter(); }
        };
        dateAuditStart.addPropertyChangeListener(dateListener);
        dateAuditEnd.addPropertyChangeListener(dateListener);

        btns[3].addActionListener(e -> loadData()); // Refresh Emp
        
        btns[0].addActionListener(e -> { // Add
            new EmployeeForm(this, null, currentUser).setVisible(true); 
            loadData(); loadAuditData(); 
        });
        
        btns[1].addActionListener(e -> { // Edit
            int row = tableEmployee.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!"); return; }
            int modelRow = tableEmployee.convertRowIndexToModel(row);
            Employee selectedEmp = currentList.get(modelRow);
            new EmployeeForm(this, selectedEmp, currentUser).setVisible(true); 
            loadData(); loadAuditData();
        });
        
        btns[2].addActionListener(e -> { // Delete
            int row = tableEmployee.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!"); return; }
            int modelRow = tableEmployee.convertRowIndexToModel(row);
            Employee selectedEmp = currentList.get(modelRow);
            if (JOptionPane.showConfirmDialog(this, "Nonaktifkan karyawan " + selectedEmp.getNamaLengkap() + "?", "Konfirmasi", 0) == 0) {
                if (employeeService.softDeleteEmployee(selectedEmp, currentUser.getId())) {
                    loadData(); loadAuditData();
                }
            }
        });

        btns[4].addActionListener(e -> loadAuditData()); // Refresh Audit
        btns[5].addActionListener(e -> loadUserData()); // Refresh User
        btns[6].addActionListener(e -> exportAuditToCSV()); // Export
        
        btns[7].addActionListener(e -> { // Add User
            new UserForm(this, null).setVisible(true); 
            loadUserData(); 
            // Catatan: Pop-up "Sukses Tambah Data" itu munculnya dari dalam UserForm.java pas lu klik tombol Simpan ya bro!
        });
        
        btns[8].addActionListener(e -> { // Edit User
            int row = tableUser.getSelectedRow();
            // 🔥 Validasi kalau belum milih data
            if (row == -1) { 
                JOptionPane.showMessageDialog(this, "Pilih akun di tabel dulu yang mau diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE); 
                return; 
            }
            new UserForm(this, userList.get(row)).setVisible(true); 
            loadUserData(); 
        });
        
        btns[9].addActionListener(e -> { // Delete User
            int row = tableUser.getSelectedRow();
            // 🔥 Validasi kalau belum milih data
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih akun di tabel dulu yang mau dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User selectedUser = userList.get(row);
            
            // 🔥 Validasi Dewa: Mencegah Super Admin hapus diri sendiri
            if (selectedUser.getId().equals(currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Tindakan Ditolak: Lu nggak bisa menghapus akun yang sedang lu pakai saat ini!", "Error Keamanan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 🔥 Pop-up Konfirmasi
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah lu yakin mau menghapus permanen akun '" + selectedUser.getUsername() + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (userService.deleteUser(selectedUser.getId())) {
                    // 🔥 Pop-up Sukses Hapus
                    JOptionPane.showMessageDialog(this, "Akun berhasil dihapus secara permanen!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadUserData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus akun dari Cloud Database!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnLogout.addActionListener(e -> { this.dispose(); new LoginForm().setVisible(true); });
    }

    private void applyRolePermissions() {
        String role = currentUser.getRole().toLowerCase();
        if (role.equals("hrd")) { 
            tabPane.setEnabledAt(1, false); 
            tabPane.setEnabledAt(2, false); 
        } else if (role.equals("auditor")) { 
            btnAdd.setEnabled(false); btnEdit.setEnabled(false); btnDelete.setEnabled(false); 
            tabPane.setEnabledAt(2, false); 
        }
    }
    
    private void applyEmployeeFilter() {
        String searchTxt = txtSearchEmp.getText().toLowerCase();
        String status = cmbFilterStatus.getSelectedItem().toString();
        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        if (searchTxt.trim().length() > 0) filters.add(RowFilter.regexFilter("(?i)" + searchTxt, 1, 2));
        if (status.equals("Aktif")) filters.add(RowFilter.regexFilter("(?i)Tetap|Kontrak", 5));
        else if (status.equals("Terminated")) filters.add(RowFilter.regexFilter("(?i)Terminated", 5));
        sorterEmployee.setRowFilter(RowFilter.andFilter(filters));
        
        // Detail: Counter update
        lblCountEmp.setText("Menampilkan: " + tableEmployee.getRowCount() + " data");
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

        if (!startDate.isEmpty() || !endDate.isEmpty()) {
            filters.add(new RowFilter<Object, Object>() {
                @Override public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    String rowDate = entry.getStringValue(0); 
                    if (rowDate == null || rowDate.length() < 10) return false;
                    String dateOnly = rowDate.substring(0, 10);
                    boolean matches = true;
                    if (!startDate.isEmpty()) matches &= (dateOnly.compareTo(startDate) >= 0);
                    if (!endDate.isEmpty()) matches &= (dateOnly.compareTo(endDate) <= 0);
                    return matches;
                }
            });
        }
        sorterAudit.setRowFilter(RowFilter.andFilter(filters));
        lblCountAudit.setText("Menampilkan: " + tableAudit.getRowCount() + " log");
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new java.util.Date());
        fileChooser.setSelectedFile(new java.io.File("Audit_Trail_" + timeStamp + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println("Waktu,Pelaku,Karyawan,Aksi,Field,Lama,Baru");
                for (int i = 0; i < tableAudit.getRowCount(); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < tableAudit.getColumnCount(); j++) {
                        sb.append(tableAudit.getValueAt(i, j).toString().replace(",", ";")).append(j == 6 ? "" : ",");
                    }
                    writer.println(sb.toString());
                }
                JOptionPane.showMessageDialog(this, "Export Berhasil!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }
    
    private void loadUserData() {
        userModel.setRowCount(0);
        userList = userService.getUsers();
        for (User u : userList) userModel.addRow(new Object[]{u.getId(), u.getUsername(), "********", u.getRole()});
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