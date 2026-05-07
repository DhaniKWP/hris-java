/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.SupabaseConfig;
import entity.Employee;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.Instant;
/**
 *
 * @author macbook
 */
public class EmployeeService {
    
    public String getEmployeesJson() {
        try {
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/employees?select=*");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
    
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<Employee> getEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        try {
            String json = getEmployeesJson();
            JSONArray arr = new JSONArray(json);
            
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Employee emp = new Employee(
                        String.valueOf(obj.get("id")), // 🔥 Ambil sebagai String (UUID)
                        obj.getString("nik"),
                        obj.getString("nama_lengkap"),
                        obj.getString("jabatan"),
                        obj.getString("departemen"),
                        obj.getString("status_kerja")
                );
                list.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean addEmployee(Employee emp) {
        try {
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/employees");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonInput = "{"
                    + "\"nik\":\"" + emp.getNik() + "\","
                    + "\"nama_lengkap\":\"" + emp.getNamaLengkap() + "\","
                    + "\"jabatan\":\"" + emp.getJabatan() + "\","
                    + "\"departemen\":\"" + emp.getDepartemen() + "\","
                    + "\"status_kerja\":\"" + emp.getStatusKerja() + "\""
                    + "}";

            conn.getOutputStream().write(jsonInput.getBytes());
            conn.getOutputStream().flush();

            int responseCode = conn.getResponseCode();
            if (responseCode == 201 || responseCode == 200) return true;
            
            // Baca error jika gagal
            printSupabaseError(conn);
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
   public boolean updateEmployeeById(String id, Employee emp, String loggedInUserId) {
        try {
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/employees");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); 
            conn.setDoOutput(true);

            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "resolution=merge-duplicates");
            
            // 🔥 INI DIA! Mengirim UUID user yang sedang login ke Supabase
            conn.setRequestProperty("x-user-id", loggedInUserId);

            String jsonInput = "{"
                    + "\"id\":\"" + id + "\","
                    + "\"nik\":\"" + emp.getNik() + "\","
                    + "\"nama_lengkap\":\"" + emp.getNamaLengkap() + "\","
                    + "\"jabatan\":\"" + emp.getJabatan() + "\","
                    + "\"departemen\":\"" + emp.getDepartemen() + "\","
                    + "\"status_kerja\":\"" + emp.getStatusKerja() + "\""
                    + "}";

            conn.getOutputStream().write(jsonInput.getBytes());
            conn.getOutputStream().flush();

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) return true;

            printSupabaseError(conn);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Tambahkan parameter loggedInUserId
    public boolean softDeleteEmployee(Employee emp, String loggedInUserId) {
        try {
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/employees");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "resolution=merge-duplicates"); 
            conn.setRequestProperty("x-user-id", loggedInUserId);

            // 🔥 Ambil waktu saat ini dengan format ISO 8601 (standar database)
            String waktuDelete = Instant.now().toString();

            // 🔥 Tambahkan "deleted_at" ke dalam JSON yang dikirim
            String jsonInput = "{"
                    + "\"id\":\"" + emp.getId() + "\","
                    + "\"nik\":\"" + emp.getNik() + "\","
                    + "\"nama_lengkap\":\"" + emp.getNamaLengkap() + "\","
                    + "\"jabatan\":\"" + emp.getJabatan() + "\","
                    + "\"departemen\":\"" + emp.getDepartemen() + "\","
                    + "\"status_kerja\":\"Terminated\","
                    + "\"deleted_at\":\"" + waktuDelete + "\"" 
                    + "}";

            conn.getOutputStream().write(jsonInput.getBytes());
            conn.getOutputStream().flush();

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) return true;

            printSupabaseError(conn);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method untuk print error dari Supabase ke output NetBeans
    private void printSupabaseError(HttpURLConnection conn) {
        try {
            BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String line;
            System.out.println("❌ ERROR DARI SUPABASE:");
            while ((line = err.readLine()) != null) {
                System.out.println(line);
            }
            err.close();
        } catch (Exception ex) {
            System.out.println("Gagal membaca error stream.");
        }
    }
}