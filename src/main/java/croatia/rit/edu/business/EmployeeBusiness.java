package croatia.rit.edu.business;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import companydata.DataLayer;
import companydata.Department;
import companydata.Employee;

public class EmployeeBusiness {

    private DataLayer dataLayer = new DataLayer("jl8592");

    public List<Employee> getAllEmployee(String companyName) {
        return dataLayer.getAllEmployee(companyName);
    }

    public Employee getEmployee(int emp_id) {
        return dataLayer.getEmployee(emp_id);
    }

    public Employee insertEmployee(Employee employee, String company) {
        // Debug log to check values
        System.out.println("Company: " + company);
        System.out.println("Dept ID: " + employee.getDeptId());

        int deptId = employee.getDeptId();
        int mngId = employee.getMngId();

        // Convert java.sql.Date to LocalDate
        java.sql.Date hireDateAsDate = employee.getHireDate();
        LocalDate hireDate = hireDateAsDate.toLocalDate();

        String empNo = employee.getEmpNo();

        // Ensure DataLayer is initialized
        if (dataLayer == null) {
            throw new IllegalStateException("DataLayer is not initialized.");
        }

        // Validation 1: dept_id must exist as a Department in your company
        System.out.println("Checking if Department exists for deptId: " + deptId + " in company: " + company);
        Department department = dataLayer.getDepartment(company, deptId);
        if (department == null) {
            throw new IllegalArgumentException(
                    "Department ID " + deptId + " does not exist in company " + company + ".");
        }
        System.out.println("Department found: " + department.getDeptName());

        // Validation 2: mng_id must be the record id of an existing Employee in your
        // company
        if (mngId != 0) { // Assuming 0 means no manager
            System.out.println("Checking if Manager exists for mngId: " + mngId);

            // Retrieve the manager's employee record
            Employee manager = dataLayer.getEmployee(mngId);

            // Check if the manager exists
            if (manager == null) {
                throw new IllegalArgumentException("Manager ID " + mngId + " does not exist.");
            }

            // Use the existing getDepartment method to check the manager's department and
            // company
            Department managerDepartment = dataLayer.getDepartment(company, manager.getDeptId());
            if (managerDepartment == null) {
                throw new IllegalArgumentException(
                        "Manager ID " + mngId + " does not belong to company " + company + ".");
            }

            System.out.println("Manager found in company: " + company + " with name: " + manager.getEmpName());
        }

        // Validation 3: hire_date must be a valid date equal to the current date or
        // earlier
        if (hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future.");
        }
        System.out.println("Hire date is valid.");

        // Validation 4: hire_date must be a weekday (Monday to Friday)
        DayOfWeek hireDayOfWeek = hireDate.getDayOfWeek();
        if (hireDayOfWeek == DayOfWeek.SATURDAY || hireDayOfWeek == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Hire date cannot be on a weekend.");
        }
        System.out.println("Hire date is on a valid weekday.");

        // Validation 5: emp_no must be unique amongst all employees in the database
        System.out.println("Checking if Employee number is unique: " + empNo);
        List<Employee> allEmployees = dataLayer.getAllEmployee(company);
        for (Employee emp : allEmployees) {
            if (emp.getEmpNo().equals(empNo)) {
                throw new IllegalArgumentException("Employee number " + empNo + " already exists in the company.");
            }
        }
        System.out.println("Employee number is unique.");

        // If all validations pass, insert the employee
        System.out.println("Inserting employee into database.");
        return dataLayer.insertEmployee(employee);
    }


    public Employee updateEmployee(Employee employee, String company) {
        // Validate if emp_id exists
        Employee existingEmployee = dataLayer.getEmployee(employee.getId());
        if (existingEmployee == null) {
            throw new IllegalArgumentException("Employee ID " + employee.getId() + " does not exist.");
        }

        int deptId = employee.getDeptId();
        int mngId = employee.getMngId();

        // Convert hire date
        java.sql.Date hireDateAsDate = employee.getHireDate();
        LocalDate hireDate = hireDateAsDate.toLocalDate();

        String empNo = employee.getEmpNo();

        // Validation 1: dept_id must exist as a Department in your company
        Department department = dataLayer.getDepartment(company, deptId);
        if (department == null) {
            throw new IllegalArgumentException(
                    "Department ID " + deptId + " does not exist in company " + company + ".");
        }

        // Validation 2: mng_id must be the record id of an existing Employee in your
        // company
        if (mngId != 0) {
            Employee manager = dataLayer.getEmployee(mngId);
            if (manager == null
                    || !dataLayer.getDepartment(company, manager.getDeptId()).getCompany().equals(company)) {
                throw new IllegalArgumentException(
                        "Manager ID " + mngId + " does not belong to company " + company + ".");
            }
        }

        // Validation 3: hire_date must be a valid date equal to the current date or
        // earlier
        if (hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future.");
        }

        // Validation 4: hire_date must be a weekday (Monday to Friday)
        DayOfWeek hireDayOfWeek = hireDate.getDayOfWeek();
        if (hireDayOfWeek == DayOfWeek.SATURDAY || hireDayOfWeek == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Hire date cannot be on a weekend.");
        }

        // Validation 5: emp_no must be unique amongst all employees in the database
        // except for the current employee
        List<Employee> allEmployees = dataLayer.getAllEmployee(company);
        for (Employee emp : allEmployees) {
            if (emp.getId() != employee.getId() && emp.getEmpNo().equals(empNo)) {
                throw new IllegalArgumentException("Employee number " + empNo + " already exists in the company.");
            }
        }

        // If all validations pass, update the employee
        return dataLayer.updateEmployee(employee);
    }

    public String deleteEmployee(int empId) {
        // Check if employee exists
        Employee existingEmployee = dataLayer.getEmployee(empId);
        if (existingEmployee == null) {
            throw new IllegalArgumentException("Employee ID " + empId + " does not exist.");
        }

        // Delete the employee
        int rowsDeleted = dataLayer.deleteEmployee(empId);
        if (rowsDeleted == 0) {
            throw new IllegalStateException("Failed to delete employee with ID " + empId);
        }

        // Return success message
        return "Employee " + empId + " deleted.";
    }

}
