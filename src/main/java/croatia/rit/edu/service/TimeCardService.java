package croatia.rit.edu.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import croatia.rit.edu.business.TimeCardBusiness;
import companydata.Timecard;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class TimeCardService {

    private TimeCardBusiness timeCardBusiness = new TimeCardBusiness();
    private Gson gson = new Gson();

    @GET
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecard(@QueryParam("timecard_id") int timecardId) {
        try {
            // Retrieve the Timecard using the business layer
            Timecard timecard = timeCardBusiness.getTimecard(timecardId);

            // Wrap in a "timecard" key
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("timecard", timecard);

            // Return the response as JSON
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
                            "{\"error\":\"An error occurred while retrieving the timecard: " + e.toString() + "\"}"))
                    .build();
        }
    }

    @GET
    @Path("/timecards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecardsForEmployee(@QueryParam("emp_id") int empId) {
        try {
            // Retrieve the Timecards for an employee using the business layer
            List<Timecard> timecards = timeCardBusiness.getTimecardsForEmployee(empId);

            // Wrap the list in JSON
            String json = gson.toJson(timecards);
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
                            "{\"error\":\"An error occurred while retrieving the timecards: " + e.toString() + "\"}"))
                    .build();
        }
    }

    @POST
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTimecard(
            @FormParam("emp_id") int empId,
            @FormParam("start_time") String startTimeStr,
            @FormParam("end_time") String endTimeStr) {
        try {
            // Convert start_time and end_time strings to Timestamps
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp startTime = new Timestamp(dateFormat.parse(startTimeStr).getTime());
            Timestamp endTime = new Timestamp(dateFormat.parse(endTimeStr).getTime());

            // Insert the timecard using TimeCardBusiness
            Timecard newTimecard = timeCardBusiness.insertTimecard(startTime, endTime, empId);

            // Prepare the success response with "success" key
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", newTimecard);

            // Return the response as JSON
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
                            "{\"error\":\"An error occurred while creating the timecard: " + e.toString() + "\"}"))
                    .build();
        }
    }

    @PUT
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTimecard(Map<String, Object> timecardData) {
        try {
            // Extract and parse input data
            int timecardId = ((Number) timecardData.get("timecard_id")).intValue();
            String startTimeStr = (String) timecardData.get("start_time");
            String endTimeStr = (String) timecardData.get("end_time");

            // Convert start_time and end_time to Timestamps
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp startTime = new Timestamp(dateFormat.parse(startTimeStr).getTime());
            Timestamp endTime = new Timestamp(dateFormat.parse(endTimeStr).getTime());

            // Update the timecard using TimeCardBusiness
            Timecard updatedTimecard = timeCardBusiness.updateTimecard(timecardId, startTime, endTime);

            // Prepare the success response with "success" key
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", updatedTimecard);

            // Return the response as JSON
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
                            "{\"error\":\"An error occurred while updating the timecard: " + e.toString() + "\"}"))
                    .build();
        }
    }

    @DELETE
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTimecard(@QueryParam("timecard_id") int timecardId) {
        try {
            // Delete the timecard using TimeCardBusiness
            String successMessage = timeCardBusiness.deleteTimecard(timecardId);

            // Prepare response with "success" key
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("success", successMessage);

            // Return the success response
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
                            "{\"error\":\"An error occurred while deleting the timecard: " + e.toString() + "\"}"))
                    .build();
        }
    }
}
