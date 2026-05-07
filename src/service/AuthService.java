/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.SupabaseConfig;
import entity.User;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author macbook
 */
public class AuthService {
    public User login(String username, String password) {
        try {
            System.out.println("========== CEK LOGIN ==========");
            System.out.println("1. Mencoba login dengan Username: '" + username + "' & Password: '" + password + "'");

            String urlUserStr = SupabaseConfig.URL + "/rest/v1/users?username=eq." + username + "&password=eq." + password;
            URL urlUser = new URL(urlUserStr);
            System.out.println("2. Tembak URL: " + urlUserStr);

            HttpURLConnection conn1 = (HttpURLConnection) urlUser.openConnection();
            conn1.setRequestMethod("GET");
            conn1.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn1.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);

            int responseCode = conn1.getResponseCode();
            System.out.println("3. Response Code Server: " + responseCode);

            if (responseCode == 200) {
                BufferedReader in1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
                StringBuilder res1 = new StringBuilder();
                String line1;
                while ((line1 = in1.readLine()) != null) res1.append(line1);
                in1.close();

                System.out.println("4. Data dari Server: " + res1.toString());

                JSONArray jsonUsers = new JSONArray(res1.toString());
                
                if (jsonUsers.length() == 0) {
                    System.out.println("GAGAL: Data kosong []. User tidak ditemukan ATAU ke-block RLS Supabase!");
                    return null;
                }

                JSONObject userObj = jsonUsers.getJSONObject(0);
                String userId = userObj.getString("id");
                String dbUser = userObj.getString("username");
                int roleId = userObj.getInt("role_id");
                
                System.out.println("5. User Ketemu! ID: " + userId + " | Role ID: " + roleId);

                // STEP 2: Ambil Role
                String urlRoleStr = SupabaseConfig.URL + "/rest/v1/roles?id=eq." + roleId;
                URL urlRole = new URL(urlRoleStr);

                HttpURLConnection conn2 = (HttpURLConnection) urlRole.openConnection();
                conn2.setRequestMethod("GET");
                conn2.setRequestProperty("apikey", SupabaseConfig.API_KEY);
                conn2.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);

                if (conn2.getResponseCode() == 200) {
                    BufferedReader in2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                    StringBuilder res2 = new StringBuilder();
                    String line2;
                    while ((line2 = in2.readLine()) != null) res2.append(line2);
                    in2.close();

                    System.out.println("6. Data Role dari Server: " + res2.toString());

                    JSONArray jsonRoles = new JSONArray(res2.toString());
                    if (jsonRoles.length() > 0) {
                        JSONObject roleObj = jsonRoles.getJSONObject(0);
                        String roleName = roleObj.getString("name"); 
                        System.out.println("✅ SUKSES: Berhasil login sebagai " + roleName);

                        return new User(userId, dbUser, roleName);
                    }
                }
            } else {
                System.out.println("❌ ERROR: Server menolak dengan kode " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR FATAL DI JAVA:");
            e.printStackTrace();
        }
        return null;
    }
}
