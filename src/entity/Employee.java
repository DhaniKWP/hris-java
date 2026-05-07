/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author macbook
 */
public class Employee {
    private String id;
    private String nik;
    private String namaLengkap;
    private String jabatan;
    private String departemen;
    private String statusKerja;
    
    public Employee(String id,String nik, String namaLengkap, String jabatan, String departemen, String statusKerja) {
        this.id = id;
        this.nik = nik;
        this.namaLengkap = namaLengkap;
        this.jabatan = jabatan;
        this.departemen = departemen;
        this.statusKerja = statusKerja;
    }
    
    public String getId() { return id; }
    public String getNik() { return nik; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getJabatan() { return jabatan; }
    public String getDepartemen() { return departemen; }
    public String getStatusKerja() { return statusKerja; }

}
