/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author macbook
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String role;
    private int roleId;

    public User(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
    
    public User(String id, String username, String password, String role, int roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.roleId = roleId;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public int getRoleId() { return roleId; }
}
