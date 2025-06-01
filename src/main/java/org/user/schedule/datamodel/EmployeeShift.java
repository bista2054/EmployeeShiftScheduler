package org.user.schedule.datamodel;

import org.user.schedule.datamodel.enums.ShiftEnum;

import java.util.Map;

public class EmployeeShift {
    private String employeeName;
    private int totalShiftsDay;
    private Map<Integer, ShiftEnum> preferredShifts;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Map<Integer, ShiftEnum> getPreferredShifts() {
        return preferredShifts;
    }

    public void setPreferredShifts(Map<Integer, ShiftEnum> preferredShifts) {
        this.preferredShifts = preferredShifts;
    }

    public int getTotalShiftsDay() {
        return totalShiftsDay;
    }

    public void setTotalShiftsDay(int totalShiftsDay) {
        this.totalShiftsDay = totalShiftsDay;
    }
}
