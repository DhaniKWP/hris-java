/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author macbook
 */
public class AuditLog {
    private String changedAt;
    private String changedByUsername;
    private String employeeName;
    private String actionType;
    private String fieldChanged;
    private String oldValue;
    private String newValue;

    public AuditLog(String changedAt, String changedByUsername, String employeeName, 
                    String actionType, String fieldChanged, String oldValue, String newValue) {
        this.changedAt = changedAt;
        this.changedByUsername = changedByUsername;
        this.employeeName = employeeName;
        this.actionType = actionType;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getChangedAt() { return changedAt; }
    public String getChangedByUsername() { return changedByUsername; }
    public String getEmployeeName() { return employeeName; }
    public String getActionType() { return actionType; }
    public String getFieldChanged() { return fieldChanged; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
}
