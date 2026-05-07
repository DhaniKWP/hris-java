/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package form;

import entity.Employee;
import entity.User; // 🔥 Jangan lupa import User
import service.EmployeeService;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author macbook
 */
public class EmployeeForm extends JDialog {
    private JTextField txtNik = new JTextField();
    private JTextField txtNama = new JTextField();
    private JTextField txtJabatan = new JTextField();
    private JTextField txtDepartemen = new JTextField();
    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Tetap", "Kontrak", "Terminated"});
    private JButton btnSave = new JButton("Simpan");
    
    private EmployeeService service = new EmployeeService();
    private Employee employeeToEdit = null; 
    private User currentUser; // 🔥 Buat nampung user yang lagi login

    // 🔥 Constructor sekarang minta 3 data: parent, employee, sama user
    public EmployeeForm(JFrame parent, Employee emp, User currentUser) {
        super(parent, emp == null ? "Tambah Data Karyawan" : "Edit Data Karyawan", true);
        this.employeeToEdit = emp;
        this.currentUser = currentUser; // Simpan data user
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel(" NIK:")); add(txtNik);
        add(new JLabel(" Nama Lengkap:")); add(txtNama);
        add(new JLabel(" Jabatan:")); add(txtJabatan);
        add(new JLabel(" Departemen:")); add(txtDepartemen);
        add(new JLabel(" Status Kerja:")); add(cmbStatus);
        add(new JLabel("")); add(btnSave);

        if (emp != null) {
            txtNik.setText(emp.getNik());
            txtNama.setText(emp.getNamaLengkap());
            txtJabatan.setText(emp.getJabatan());
            txtDepartemen.setText(emp.getDepartemen());
            cmbStatus.setSelectedItem(emp.getStatusKerja());
        }

        btnSave.addActionListener(e -> saveData());
    }

    private void saveData() {
        // 1. Ambil data dari textfield dan hapus spasi di awal/akhir pakai trim()
        String nik = txtNik.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = txtJabatan.getText().trim();
        String dept = txtDepartemen.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        // 2. 🔥 VALIDASI KETAT: Cek apakah ada kolom yang kosong
        if (nik.isEmpty() || nama.isEmpty() || jabatan.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Validasi Gagal: Semua kolom (NIK, Nama, Jabatan, Departemen) wajib diisi!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Berhenti di sini, jangan lanjut ke database
        }

        // 3. 🔥 VALIDASI TAMBAHAN: Pastikan NIK hanya berisi angka
        if (!nik.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, 
                "Validasi Gagal: NIK harus berupa angka, tidak boleh ada huruf atau simbol!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Berhenti di sini
        }

        // Lanjut ke proses simpan (Kalau semua validasi di atas lolos)
        boolean success;
        if (employeeToEdit == null) {
            // Mode Tambah
            Employee newEmp = new Employee("", nik, nama, jabatan, dept, status);
            success = service.addEmployee(newEmp);
        } else {
            // Mode Edit
            Employee updatedEmp = new Employee(employeeToEdit.getId(), nik, nama, jabatan, dept, status);
            success = service.updateEmployeeById(employeeToEdit.getId(), updatedEmp, currentUser.getId());
        }

        // Notifikasi hasil
        if (success) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan dengan aman!");
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke Cloud Database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
