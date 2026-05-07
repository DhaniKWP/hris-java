/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.SupabaseConfig;
import entity.AuditLog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 *
 * @author macbook
 */
public class AuditService {
    public ArrayList<AuditLog> getAuditLogs() {
        ArrayList<AuditLog> list = new ArrayList<>();
        try {
            // 🔥 URL Sakti buat JOIN tabel history, users, dan employees sekaligus!
            String endpoint = SupabaseConfig.URL + 
                "/rest/v1/employee_history?select=changed_at,action_type,field_changed,old_value,new_value,users(username),employees(nama_lengkap)&order=changed_at.desc";
            
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder res = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) res.append(line);
                in.close();

                JSONArray arr = new JSONArray(res.toString());
                
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    
                    // Parsing nested JSON hasil dari relasi tabel
                    String username = obj.has("users") && !obj.isNull("users") ? obj.getJSONObject("users").getString("username") : "Unknown";
                    String empName = obj.has("employees") && !obj.isNull("employees") ? obj.getJSONObject("employees").getString("nama_lengkap") : "Unknown";
                    
                    // Menangani field null dari database dengan optString
                    AuditLog log = new AuditLog(
                        obj.getString("changed_at").substring(0, 19).replace("T", " "), // Format tanggal biar rapi
                        username,
                        empName,
                        obj.optString("action_type", "UPDATE"),
                        obj.getString("field_changed"),
                        obj.optString("old_value", "-"),
                        obj.optString("new_value", "-")
                    );
                    
                    list.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
