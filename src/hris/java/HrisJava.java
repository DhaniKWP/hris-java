/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hris.java;

import service.EmployeeService;
import service.AuthService;
import entity.Employee;
import java.util.ArrayList;

/**
 *
 * @author macbook
 */
public class HrisJava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // 🔥 TEST LOGIN
        AuthService auth = new AuthService();
        boolean isLogin = auth.login("admin_super", "password123");
        System.out.println("Login: " + isLogin);

        // 🔥 TEST GET DATA
        EmployeeService service = new EmployeeService();
        ArrayList<Employee> list = service.getEmployees();

        System.out.println("Data Karyawan:");
        for (Employee e : list) {
            System.out.println(e.getNamaLengkap());
        }

        // 🔥 TEST INSERT DATA
        Employee emp = new Employee("999", "TEST USER", "IT", "Tech", "Tetap");
        service.addEmployee(emp);

        System.out.println("Insert selesai!");
    }
    
}
