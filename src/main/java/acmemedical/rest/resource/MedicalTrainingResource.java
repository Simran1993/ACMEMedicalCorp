package acmemedical.rest.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.USER_ROLE;
import jakarta.ws.rs.core.Response.Status;
import static acmemedical.utility.MyConstants.MEDICAL_TRAINING_RESOURCE_NAME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;
import acmemedical.utility.MyConstants;


@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalTrainingResource {

	private static final Logger LOG = LogManager.getLogger(MedicalTrainingResource.class);
	private static final String GET_ALL_MEDICAL_TRAINING = "MedicalTraining.findAll";

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicalTrainings() {
        List<MedicalTraining> medicalTrainings = service.getAll(MedicalTraining.class, GET_ALL_MEDICAL_TRAINING);
        return Response.ok(medicalTrainings).build();
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(MyConstants.RESOURCE_PATH_ID_PATH)
    public Response getMedicalTrainingById(@PathParam(MyConstants.RESOURCE_PATH_ID_ELEMENT) int id) {
        MedicalTraining medicalTraining = service.getMedicalTrainingById(id);
        return Response.status(medicalTraining == null ? Status.BAD_REQUEST : Status.OK).entity(medicalTraining).build();
    }
    
    
    @POST
	@RolesAllowed({ADMIN_ROLE})
	public Response addMedicalTraining(MedicalTraining newMedicalTraining) {
    	MedicalTraining createdMedicalTraining = service.persistMedicalTraining(newMedicalTraining);
        return Response.status(Status.CREATED).entity(createdMedicalTraining).build();
	}
	
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(MyConstants.RESOURCE_PATH_ID_PATH)
    public Response updateMedicalTraining(@PathParam("id") int id, MedicalTraining updatedMedicalTraining) {
        MedicalTraining existingMedicalTraining = service.getMedicalTrainingById(id);
        if (existingMedicalTraining == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        updatedMedicalTraining.setId(id); // Ensure the ID is set correctly
        MedicalTraining updatedTraining = service.persistMedicalTraining(updatedMedicalTraining);
        return Response.ok(updatedTraining).build();
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(MyConstants.RESOURCE_PATH_ID_PATH)
    public Response deleteMedicalTraining(@PathParam("id") int id) {
        MedicalTraining medicalTraining = service.getMedicalTrainingById(id);
        if (medicalTraining == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        service.deleteMedicalTraining(id);
        return Response.noContent().build();
    }

    
}
