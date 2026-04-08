/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.SupabaseConfig;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author macbook
 */
public class AuthService {
    public boolean login(String username, String password) {
        try {
            URL url = new URL(SupabaseConfig.URL + 
                "/rest/v1/users?username=eq." + username + "&password=eq." + password);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            String response = in.readLine();

            in.close();
            conn.disconnect();

            return response != null && !response.equals("[]");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
