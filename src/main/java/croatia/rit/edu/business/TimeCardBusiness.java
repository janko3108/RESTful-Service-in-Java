package croatia.rit.edu.business;

import companydata.DataLayer;
import companydata.Employee;
import companydata.Timecard;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TimeCardBusiness {

    private DataLayer dataLayer = new DataLayer("jl8592");

    public Timecard getTimecard(int timecardId) {
        Timecard timecard = dataLayer.getTimecard(timecardId);
        if (timecard == null) {
            throw new IllegalArgumentException("Timecard ID " + timecardId + " does not exist.");
        }
        return timecard;
    }

    public List<Timecard> getTimecardsForEmployee(int empId) {
        List<Timecard> timecards = dataLayer.getAllTimecard(empId);
        if (timecards == null || timecards.isEmpty()) {
            throw new IllegalArgumentException("No timecards found for Employee ID " + empId);
        }
        return timecards;
    }

    public Timecard insertTimecard(Timestamp startTime, Timestamp endTime, int empId) {
        // Validate that the employee exists
        Employee employee = dataLayer.getEmployee(empId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee ID " + empId + " does not exist.");
        }

        LocalDateTime startDateTime = startTime.toLocalDateTime();
        LocalDateTime endDateTime = endTime.toLocalDateTime();

        // Constraint ii: start_time must be within the past week or the current date
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        if (startDateTime.isBefore(oneWeekAgo) || startDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("start_time must be within the last week or today.");
        }

        // Constraint iii: end_time must be at least 1 hour after start_time and on the
        // same day
        if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
            throw new IllegalArgumentException("end_time must be on the same day as start_time.");
        }
        if (Duration.between(startDateTime, endDateTime).toHours() < 1) {
            throw new IllegalArgumentException("end_time must be at least 1 hour after start_time.");
        }

        // Constraint iv: start_time and end_time must be between Monday and Friday
        if (isWeekend(startDateTime) || isWeekend(endDateTime)) {
            throw new IllegalArgumentException("start_time and end_time cannot be on a weekend.");
        }

        // Constraint v: start_time and end_time must be within business hours
        LocalTime startBusiness = LocalTime.of(6, 0);
        LocalTime endBusiness = LocalTime.of(18, 0);
        if (!isWithinBusinessHours(startDateTime.toLocalTime(), startBusiness, endBusiness) ||
                !isWithinBusinessHours(endDateTime.toLocalTime(), startBusiness, endBusiness)) {
            throw new IllegalArgumentException("start_time and end_time must be between 06:00 and 18:00.");
        }

        // Constraint vi: start_time must not be on the same day as any other start_time
        // for the employee
        List<Timecard> existingTimecards = dataLayer.getAllTimecard(empId);
        for (Timecard timecard : existingTimecards) {
            if (timecard.getStartTime().toLocalDateTime().toLocalDate().equals(startDateTime.toLocalDate())) {
                throw new IllegalArgumentException("An existing timecard already has a start_time on this day.");
            }
        }

        // If all validations pass, create the Timecard
        Timecard newTimecard = new Timecard(startTime, endTime, empId);
        return dataLayer.insertTimecard(newTimecard);
    }

    public Timecard updateTimecard(int timecardId, Timestamp startTime, Timestamp endTime) {
        // Validate if the timecard exists
        Timecard existingTimecard = dataLayer.getTimecard(timecardId);
        if (existingTimecard == null) {
            throw new IllegalArgumentException("Timecard ID " + timecardId + " does not exist.");
        }

        int empId = existingTimecard.getEmpId();

        LocalDateTime startDateTime = startTime.toLocalDateTime();
        LocalDateTime endDateTime = endTime.toLocalDateTime();

        // Perform all the same validations as for creating a Timecard

        // Constraint ii: start_time must be within the past week or the current date
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        if (startDateTime.isBefore(oneWeekAgo) || startDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("start_time must be within the last week or today.");
        }

        // Constraint iii: end_time must be at least 1 hour after start_time and on the
        // same day
        if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
            throw new IllegalArgumentException("end_time must be on the same day as start_time.");
        }
        if (Duration.between(startDateTime, endDateTime).toHours() < 1) {
            throw new IllegalArgumentException("end_time must be at least 1 hour after start_time.");
        }

        // Constraint iv: start_time and end_time must be between Monday and Friday
        if (isWeekend(startDateTime) || isWeekend(endDateTime)) {
            throw new IllegalArgumentException("start_time and end_time cannot be on a weekend.");
        }

        // Constraint v: start_time and end_time must be within business hours
        LocalTime startBusiness = LocalTime.of(6, 0);
        LocalTime endBusiness = LocalTime.of(18, 0);
        if (!isWithinBusinessHours(startDateTime.toLocalTime(), startBusiness, endBusiness) ||
                !isWithinBusinessHours(endDateTime.toLocalTime(), startBusiness, endBusiness)) {
            throw new IllegalArgumentException("start_time and end_time must be between 06:00 and 18:00.");
        }

        // Constraint vi: start_time must not be on the same day as any other start_time
        // for that employee
        List<Timecard> existingTimecards = dataLayer.getAllTimecard(empId);
        for (Timecard timecard : existingTimecards) {
            if (timecard.getStartTime().toLocalDateTime().toLocalDate().equals(startDateTime.toLocalDate()) &&
                    timecard.getId() != timecardId) {
                throw new IllegalArgumentException("An existing timecard already has a start_time on this day.");
            }
        }

        // If all validations pass, update the Timecard
        existingTimecard.setStartTime(startTime);
        existingTimecard.setEndTime(endTime);
        return dataLayer.updateTimecard(existingTimecard);
    }

    public String deleteTimecard(int timecardId) {
        // Check if the timecard exists
        Timecard existingTimecard = dataLayer.getTimecard(timecardId);
        if (existingTimecard == null) {
            throw new IllegalArgumentException("Timecard ID " + timecardId + " does not exist.");
        }

        // Delete the timecard
        int rowsDeleted = dataLayer.deleteTimecard(timecardId);
        if (rowsDeleted == 0) {
            throw new IllegalStateException("Failed to delete timecard with ID " + timecardId);
        }

        // Return success message
        return "Timecard " + timecardId + " deleted.";
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isWithinBusinessHours(LocalTime time, LocalTime startBusiness, LocalTime endBusiness) {
        return !time.isBefore(startBusiness) && !time.isAfter(endBusiness);
    }
}
