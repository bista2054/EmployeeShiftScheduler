package org.user.schedule.processor;

import org.user.schedule.datamodel.EmployeeShift;
import org.user.schedule.datamodel.enums.ShiftEnum;

import java.util.*;

import static org.user.schedule.util.DaysOfTheWeek.DAYS;

public abstract class ShiftAssigner {
    protected List<EmployeeShift> employeeShifts = new ArrayList<>();
    protected Map<String, Map<String, List<String>>> weeklySchedules = new HashMap<>();
    private static final int MINIMUM_EMPLOYEE_PER_SHIFT = 2;
    private static final int MAX_SHIFTS_PER_WEEK = 5;

    public abstract void assignShifts(String employeeName, Map<Integer, ShiftEnum> preferredShifts);
    public abstract void printWeeklySchedules();

    public void createSchedule() {
        initializeWeeklySchedule();

        // Assign shifts in priority order
        for (int priority = 1; priority <= 3; priority++) {
            assignShiftsByPriority(priority);
        }

        // Fill remaining slots to meet minimum requirements
        ensureMinimumCoverage();

        // Assign any completely unassigned employees
        assignUnassignedEmployees();
    }

    private void initializeWeeklySchedule() {
        for (String day : DAYS) {
            Map<String, List<String>> daySchedule = new HashMap<>();
            for (ShiftEnum shift : ShiftEnum.values()) {
                daySchedule.put(shift.name(), new ArrayList<>());
            }
            weeklySchedules.put(day, daySchedule);
        }
    }

    private void assignShiftsByPriority(int priority) {
        // Shuffle days to distribute assignments more evenly
        List<String> shuffledDays = new ArrayList<>(Arrays.asList(DAYS));
        Collections.shuffle(shuffledDays);

        for (String day : shuffledDays) {
            for (ShiftEnum shift : ShiftEnum.values()) {
                List<String> employeesOnShift = weeklySchedules.get(day).get(shift.name());

                // Find employees who prefer this shift at current priority level
                List<EmployeeShift> eligibleEmployees = new ArrayList<>();
                for (EmployeeShift employee : employeeShifts) {
                    if (canAssignShift(employee, day, shift, priority)) {
                        eligibleEmployees.add(employee);
                    }
                }

                // Assign up to MINIMUM_EMPLOYEE_PER_SHIFT employees
                for (EmployeeShift employee : eligibleEmployees) {
                    if (employeesOnShift.size() >= MINIMUM_EMPLOYEE_PER_SHIFT) break;

                    assignShift(employee, day, shift);
                }
            }
        }
    }

    private boolean canAssignShift(EmployeeShift employee, String day, ShiftEnum shift, int priority) {
        // Check if employee has this shift at the given priority (and it's not null)
        if (employee.getPreferredShifts().get(priority) == null ||
                !shift.equals(employee.getPreferredShifts().get(priority))) {
            return false;
        }

        // Check if employee is already assigned to this day
        if (isEmployeeAssignedToDay(employee, day)) {
            return false;
        }

        // Check if employee hasn't exceeded weekly limit
        return employee.getTotalShiftsDay() < MAX_SHIFTS_PER_WEEK;
    }

    private boolean isEmployeeAssignedToDay(EmployeeShift employee, String day) {
        for (List<String> shiftEmployees : weeklySchedules.get(day).values()) {
            if (shiftEmployees.contains(employee.getEmployeeName())) {
                return true;
            }
        }
        return false;
    }

    private void assignShift(EmployeeShift employee, String day, ShiftEnum shift) {
        weeklySchedules.get(day).get(shift.name()).add(employee.getEmployeeName());
        employee.setTotalShiftsDay(employee.getTotalShiftsDay() + 1);
    }

    private void ensureMinimumCoverage() {
        for (String day : DAYS) {
            for (ShiftEnum shift : ShiftEnum.values()) {
                List<String> employeesOnShift = weeklySchedules.get(day).get(shift.name());

                while (employeesOnShift.size() < MINIMUM_EMPLOYEE_PER_SHIFT) {
                    EmployeeShift availableEmployee = findAvailableEmployee(day);
                    if (availableEmployee == null) break; // No more available employees

                    assignShift(availableEmployee, day, shift);
                }
            }
        }
    }

    private void assignUnassignedEmployees() {
        for (EmployeeShift employee : employeeShifts) {
            if (employee.getTotalShiftsDay() == 0) {
                // Employee wasn't assigned to any shifts - find first available
                for (String day : DAYS) {
                    if (!isEmployeeAssignedToDay(employee, day) &&
                            employee.getTotalShiftsDay() < MAX_SHIFTS_PER_WEEK) {

                        // Find first shift with capacity
                        for (ShiftEnum shift : ShiftEnum.values()) {
                            assignShift(employee, day, shift);
                            break; // Assign to just one shift
                        }
                        break;
                    }
                }
            }
        }
    }

    private EmployeeShift findAvailableEmployee(String day) {
        List<EmployeeShift> availableEmployees = new ArrayList<>();

        for (EmployeeShift employee : employeeShifts) {
            if (!isEmployeeAssignedToDay(employee, day) &&
                    employee.getTotalShiftsDay() < MAX_SHIFTS_PER_WEEK) {
                availableEmployees.add(employee);
            }
        }

        if (availableEmployees.isEmpty()) {
            return null;
        }

        // Return random available employee
        return availableEmployees.get(new Random().nextInt(availableEmployees.size()));
    }
}