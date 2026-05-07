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
import java.util.ArrayList;
/**
 *
 * @author macbook
 */
public class UserService {
    public ArrayList<User> getUsers() {
        ArrayList<User> list = new ArrayList<>();
        try {
            // Join dengan tabel roles untuk dapetin nama rolenya
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/users?select=id,username,password,role_id,roles(name)");
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
                    String roleName = obj.has("roles") && !obj.isNull("roles") ? 
                                      obj.getJSONObject("roles").getString("name") : "Unknown";
                    
                    list.add(new User(
                        obj.getString("id"),
                        obj.getString("username"),
                        obj.getString("password"),
                        roleName,
                        obj.getInt("role_id")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean saveOrUpdateUser(User u) {
        try {
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "resolution=merge-duplicates"); // UPSERT Mode

            // Jika Insert, abaikan ID (biar digenerate Supabase). Jika Update, sertakan ID.
            String jsonInput = "{";
            if (u.getId() != null && !u.getId().isEmpty()) {
                jsonInput += "\"id\":\"" + u.getId() + "\",";
            }
            jsonInput += "\"username\":\"" + u.getUsername() + "\","
                       + "\"password\":\"" + u.getPassword() + "\","
                       + "\"role_id\":" + u.getRoleId() + "}";

            conn.getOutputStream().write(jsonInput.getBytes());
            conn.getOutputStream().flush();

            int code = conn.getResponseCode();
            return (code >= 200 && code < 300);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String id) {
        try {
            URL url = new URL(SupabaseConfig.URL + "/rest/v1/users?id=eq." + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE"); // Hapus permanen
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);

            int code = conn.getResponseCode();
            return (code >= 200 && code < 300);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
