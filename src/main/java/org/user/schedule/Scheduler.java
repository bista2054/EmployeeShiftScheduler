package org.user.schedule;

import org.user.schedule.datamodel.EmployeeShift;
import org.user.schedule.datamodel.enums.ShiftEnum;
import org.user.schedule.processor.ShiftAssigner;
import org.user.schedule.util.DaysOfTheWeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.user.schedule.util.DaysOfTheWeek.DAYS;

public class Scheduler extends ShiftAssigner {
    public static void main(String[] args) {
        //read file from resources
        String fileName1 = "weeklyshift.txt";
        String fileName2 = "weeklyshift2.txt";
        for (int i = 1; i <= 2; i++) {
            String fileName = (i == 1) ? fileName1 : fileName2;
            System.out.println("\n****** Processing File: " + fileName + " for scenario " + i + " ******");
            scheduleAssigner(fileName);
        }
    }

    private static void scheduleAssigner(String fileName) {
        Scheduler scheduler = new Scheduler();
        try (InputStream inputStream = Scheduler.class.getClassLoader().getResourceAsStream(fileName);

             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // Read and print the file content
            String line;
            while ((line = reader.readLine()) != null) {
                String [] parts = line.split(",");
                if (parts.length < 2) {
                    System.err.println("Invalid line format: " + line);
                    continue;
                }
                String employeeName = parts[0].trim();
                Map<Integer, ShiftEnum> preferredShifts = new HashMap<>();

                // Handle preferences (1-3) with null checks
                for (int i = 1; i <= 3; i++) {
                    if (i < parts.length && !parts[i].trim().isEmpty()) {
                        try {
                            preferredShifts.put(i, ShiftEnum.valueOf(parts[i].trim().toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid shift value for " + employeeName +
                                    " at preference " + i + ": " + parts[i].trim());
                        }
                    }
                }
//                System.out.println("Employee: " + employeeName + ", Preferred Shifts: " + preferredShifts);
                scheduler.assignShifts(employeeName, preferredShifts);
            }
//            System.out.println("\n ****** Creating Weekly Schedule ******");
            scheduler.createSchedule();
            scheduler.printWeeklySchedules();

        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }


    @Override
    public void assignShifts(String employeeName, Map<Integer, ShiftEnum> preferredShifts) {
        EmployeeShift employeeShift = new EmployeeShift();
        employeeShift.setEmployeeName(employeeName);
        employeeShift.setPreferredShifts(preferredShifts);
        employeeShifts.add(employeeShift);
    }

    @Override
    public void printWeeklySchedules() {
        if (!weeklySchedules.isEmpty()) {
            System.out.println("\n****** Weekly Schedule ******");

            // Print header row with shift names
            System.out.print("| Day        |");
            for (ShiftEnum shiftEnum : ShiftEnum.values()) {
                System.out.print(" " + String.format("%-40s", shiftEnum.name()) + "|");
            }
            System.out.println();

            // Print separator line
            System.out.print("|------------|");
            for (int i = 0; i < ShiftEnum.values().length; i++) {
                System.out.print("-----------------------------------------|");
            }
            System.out.println();

            // Print each day with shifts in columns
            for (String day : DAYS) {
                if (weeklySchedules.containsKey(day)) {
                    System.out.print("| " + String.format("%-11s", day) + "|");
                    Map<String, List<String>> shifts = weeklySchedules.get(day);
                    for (ShiftEnum shiftEnum : ShiftEnum.values()) {
                        List<String> employees = shifts.getOrDefault(shiftEnum.name(), List.of());
                        System.out.print(" " + String.format("%-40s", employees) + "|");
                    }
                    System.out.println();
                }
            }
        } else {
            System.out.println("No shifts assigned yet.");
        }
    }
}