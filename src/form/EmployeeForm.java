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
        String nik = txtNik.getText();
        String nama = txtNama.getText();
        String jabatan = txtJabatan.getText();
        String dept = txtDepartemen.getText();
        String status = cmbStatus.getSelectedItem().toString();

        if (nik.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIK dan Nama tidak boleh kosong!");
            return;
        }

        boolean success;
        if (employeeToEdit == null) {
            // Mode Tambah
            Employee newEmp = new Employee("", nik, nama, jabatan, dept, status);
            success = service.addEmployee(newEmp);
        } else {
            // Mode Edit
            Employee updatedEmp = new Employee(employeeToEdit.getId(), nik, nama, jabatan, dept, status);
            // 🔥 MERAHNYA ILANG DISINI! Kita kasih parameter ke-3: currentUser.getId()
            success = service.updateEmployeeById(employeeToEdit.getId(), updatedEmp, currentUser.getId());
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke Cloud!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
