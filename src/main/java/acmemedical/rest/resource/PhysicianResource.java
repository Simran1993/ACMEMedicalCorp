/******************************************************************************************************** * File:  PhysicianResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 */
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH;
import static acmemedical.utility.MyConstants.PHYSICIAN_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmemedical.utility.MyConstants.USER_ROLE;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;
import org.hibernate.StaleObjectStateException;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import acmemedical.entity.SecurityUser;
import acmemedical.utility.MyConstants;
import acmemedical.entity.Physician;

@Path(PHYSICIAN_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PhysicianResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    //Only a user with the SecurityRole ‘ADMIN_ROLE’ can get the list of all physicians.
    @RolesAllowed({ADMIN_ROLE})
    public Response getPhysicians() {
        LOG.debug("retrieving all physicians ...");
        List<Physician> physicians = service.getAllPhysicians();
        Response response = Response.ok(physicians).build();
        return response;
    }

    @GET
    //A user with either the role ‘ADMIN_ROLE’ or ‘USER_ROLE’ can get a specific physician.
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPhysicianById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific physician " + id);
        Response response = null;
        Physician physician = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
        	physician = service.getPhysicianById(id);
            response = Response.status(physician == null ? Status.NOT_FOUND : Status.OK).entity(physician).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            physician = sUser.getPhysician();
            if (physician != null && physician.getId() == id) {
                response = Response.status(Status.OK).entity(physician).build();
            } else {
            	//disallows a ‘USER_ROLE’ user from getting a physician that is not linked to the SecurityUser.
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPhysician(Physician newPhysician) {
        LOG.debug("PhysicianResource.addPhysician");
        Response response = null;
        Physician newPhysicianWithIdTimestamps = null;
        try {
            newPhysicianWithIdTimestamps = service.persistPhysician(newPhysician);
        } catch (Exception e) {
            LOG.error("caught exception attempting to persistPhysician", e);
            e.printStackTrace();
        }
        // Build a SecurityUser linked to the new physician
        LOG.debug("just called service.persistPhysician... new physician id is " + newPhysicianWithIdTimestamps.getId());
        try {
            service.buildUserForNewPhysician(newPhysicianWithIdTimestamps);
        } catch (Exception e) {
            LOG.error("caught exception attempting to buildUserForNewPhysician", e);
            e.printStackTrace();
        }
        LOG.debug("just finished calling buildUserForNewPhysician");
        response = Response.ok(newPhysicianWithIdTimestamps).build();
        return response;
    }

	@PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updatePhysician(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Physician updatedPhysician) {
    	LOG.debug("Updating physician " + id);
        Response response = null;
        Physician physician = service.updatePhysicianById(id, updatedPhysician);
        response = Response.ok(physician).build();
        return response;
    }
   
    @PUT
    //Only an ‘ADMIN_ROLE’ user can associate a Medicine and/or Patient to a Physician.
    @RolesAllowed({ADMIN_ROLE})
    @Path(PHYSICIAN_PATIENT_MEDICINE_RESOURCE_PATH)
    public Response updateMedicineForPhysicianPatient(@PathParam("physicianId") int physicianId, @PathParam("patientId") int patientId, Medicine newMedicine) {
        Response response = null;
        Medicine medicine = service.setMedicineForPhysicianPatient(physicianId, patientId, newMedicine);
        response = Response.ok(medicine).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deletePhysicianById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to delete specific physician " + id);
        Response response = null;
        Physician physician = service.getPhysicianById(id);
        if (physician != null) {
            service.deletePhysicianById(id);
            String msg = "Physician deleted successfully";
            response = Response.ok(msg).build();
        } else {
        	String jsonResponse = "{\"reason-phrase\":\"Physician not found\"}";
    		response = Response.status(Status.NOT_FOUND).entity(jsonResponse).build();
        }
        LOG.debug("Response: " + response.getStatus());
        return response;
    }
    

    
}