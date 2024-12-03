package croatia.rit.edu.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import croatia.rit.edu.business.EmployeeBusiness;
import companydata.Employee;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class EmployeeService {

    private EmployeeBusiness employeeBusiness = new EmployeeBusiness();
    private Gson gson = new Gson();

    @GET
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployee(@QueryParam("emp_id") int empId) {
        try {
            Employee employee = employeeBusiness.getEmployee(empId);
            if (employee != null) {
                String json = gson.toJson(employee);
                return Response.ok(json).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson("{\"error\":\"Employee not found.\"}"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson("{\"error\":\"An error occurred while retrieving the employee.\"}"))
                    .build();
        }
    }

    @GET
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees(@QueryParam("company") String companyName) {
        try {
            List<Employee> employees = employeeBusiness.getAllEmployee(companyName);
            if (employees != null && !employees.isEmpty()) {
                String json = gson.toJson(employees);
                return Response.ok(json).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson("{\"error\":\"No employees found for company.\"}"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson("{\"error\":\"An error occurred while retrieving the employees.\"}"))
                    .build();
        }
    }

    @POST
    @Path("/employee")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmployee(
            @FormParam("emp_name") String empName,
            @FormParam("emp_no") String empNo,
            @FormParam("hire_date") String hireDateStr,
            @FormParam("job") String job,
            @FormParam("salary") Double salary,
            @FormParam("dept_id") String deptId,
            @FormParam("mng_id") int mngId,
            @FormParam("company") String company) {
        try {
            // Convert hireDateStr to java.util.Date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilHireDate = sdf.parse(hireDateStr); // Proper date parsing

            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlHireDate = new java.sql.Date(utilHireDate.getTime());

            // Create Employee object
            Employee employee = new Employee(empName, empNo, sqlHireDate, job, salary, Integer.parseInt(deptId), mngId);

            // Insert the employee using EmployeeBusiness
            Employee insertedEmployee = employeeBusiness.insertEmployee(employee, company);

            // Prepare response with "success" key
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", insertedEmployee);

            // Return the inserted employee as a success response
            String json = gson.toJson(successResponse);
            return Response.ok(json).build();

        } catch (IllegalArgumentException e) {
            // Log the error message
            e.printStackTrace();
            // Return validation errors as JSON
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            String json = gson.toJson(error);
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(
                            "{\"error\":\"An error occurred while inserting the employee: " + e.toString() + "\"}"))
                    .build();
        }
    }

    @PUT
    @Path("/employee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(Map<String, Object> employeeData) {
        try {
            // Extract employee data and company separately from the JSON
            String company = (String) employeeData.get("company");

            // Safely cast numbers to appropriate types
            int empId = ((Number) employeeData.get("emp_id")).intValue();
            int deptId = ((Number) employeeData.get("dept_id")).intValue();
            int mngId = ((Number) employeeData.get("mng_id")).intValue();
            double salary = ((Number) employeeData.get("salary")).doubleValue();

            // Convert hireDate to SQL Date
            java.sql.Date hireDate = java.sql.Date.valueOf((String) employeeData.get("hire_date"));

            // Create Employee object
            Employee employee = new Employee(
                    empId,
                    (String) employeeData.get("emp_name"),
                    (String) employeeData.get("emp_no"),
                    hireDate,
                    (String) employeeData.get("job"),
                    salary, // Use the safely cast salary
                    deptId,
                    mngId);

            // Update the employee using EmployeeBusiness
            Employee updatedEmployee = employeeBusiness.updateEmployee(employee, company);

            // Prepare response with "success" key
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", updatedEmployee);

            // Return the updated employee as a success response
            String json = gson.toJson(successResponse);
            return Response.ok(json).build();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            String json = gson.toJson(error);
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(
                            "{\"error\":\"An error occurred while updating the employee: " + e.toString() + "\"}"))
                    .build();
        }
    }

    @DELETE
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(@QueryParam("emp_id") int empId) {
        try {
            // Delete the employee using EmployeeBusiness
            String successMessage = employeeBusiness.deleteEmployee(empId);

            // Prepare response with "success" key
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("success", successMessage);

            // Return the success response
            String json = gson.toJson(successResponse);
            return Response.ok(json).build();

        } catch (IllegalArgumentException e) {
            // Log the error message
            e.printStackTrace();
            // Return validation error as JSON
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            String json = gson.toJson(error);
            return Response.status(Response.Status.BAD_REQUEST).entity(json).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(
                            "{\"error\":\"An error occurred while deleting the employee: " + e.toString() + "\"}"))
                    .build();
        }
    }

}
