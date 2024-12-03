package croatia.rit.edu.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import companydata.Department;
import croatia.rit.edu.business.DepartmentBusiness;

import java.util.List;

@Path("/")
public class DepartmentService {

    private DepartmentBusiness departmentBusiness = new DepartmentBusiness();
    private Gson gson = new Gson();

    @Path("/department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int deptId) {
        Department department = departmentBusiness.getDepartment(company, deptId);
        if (department == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Department not found.\"}").build();
        }
        return Response.ok(department).build();
    }

    @Path("/departments")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDepartments(@QueryParam("company") String company) {
        List<Department> departments = departmentBusiness.getAllDepartments(company);
        if (departments == null || departments.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No departments found.\"}").build();
        }
        return Response.ok(departments).build();
    }

    @Path("/department")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDepartment(String json) {
        try {
            Department department = gson.fromJson(json, Department.class);
            
            if (department.getId() == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("{\"error\":\"Invalid department ID.\"}")
                               .build();
            }
    
            Department updatedDepartment = departmentBusiness.updateDepartment(department);
            return Response.ok(gson.toJson(updatedDepartment)).build();
    
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("{\"error\":\"%s\"}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        } catch (Exception e) {
            String errorMessage = "{\"error\":\"An unexpected error occurred.\"}";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        }
    }
    

    @Path("/department")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertDepartment(@FormParam("company") String company,
            @FormParam("dept_name") String deptName,
            @FormParam("dept_no") String deptNo,
            @FormParam("location") String location) {
        Department newDepartment = new Department(company, deptName, deptNo, location);

        try {
            Department insertedDepartment = departmentBusiness.insertDepartment(newDepartment);

            JsonObject successResponse = new JsonObject();
            JsonObject departmentDetails = new JsonObject();
            departmentDetails.addProperty("dept_id", insertedDepartment.getId()); 
            departmentDetails.addProperty("company", insertedDepartment.getCompany());
            departmentDetails.addProperty("dept_name", insertedDepartment.getDeptName());
            departmentDetails.addProperty("dept_no", insertedDepartment.getDeptNo());
            departmentDetails.addProperty("location", insertedDepartment.getLocation());

            successResponse.add("success", departmentDetails);

            return Response.ok(successResponse.toString()).build();

        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("{\"error\":\"%s\"}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        } catch (Exception e) {
            String errorMessage = "{\"error\":\"An unexpected error occurred.\"}";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        }
    }

    @Path("/department")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int deptId) {
        int rowsDeleted = departmentBusiness.deleteDepartment(company, deptId);
        if (rowsDeleted <= 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Department not found.\"}")
                    .build();
        }
        String successMessage = String.format("{\"success\":\"Department %d from %s deleted.\"}", deptId, company);
        return Response.ok(successMessage).build();
    }

}
