package edu.stevens.cs548.clinic.service.web.rest.resources;

import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.util.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.util.TreatmentDtoFactory;
import edu.stevens.cs548.clinic.service.ejb.IProviderService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.ejb.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.ejb.IProviderService.TreatmentNotFoundExn;
import edu.stevens.cs548.clinic.service.ejb.IProviderServiceLocal;
import edu.stevens.cs548.clinic.service.representations.ProviderRepresentation;
import edu.stevens.cs548.clinic.service.representations.Representation;
import edu.stevens.cs548.clinic.service.representations.TreatmentRepresentation;

import java.net.URI;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("/provider")
@RequestScoped
public class ProviderResource {
	@Inject IProviderServiceLocal providerService;
	
    @Context
    private UriInfo uriInfo;
    
    private ProviderDtoFactory providerDtoFactory;
    private TreatmentDtoFactory treatmentDtoFactory;

    /**
     * Default constructor. 
     */
    public ProviderResource() {
        // TODO Auto-generated constructor stub
    	providerDtoFactory = new ProviderDtoFactory();
    	treatmentDtoFactory = new TreatmentDtoFactory();
    }
    
    @GET
    @Path("site")
    @Produces("text/plain")
    public String getSiteInfo() {
    	return providerService.siteInfo();
    }
   

    
    
    @GET
    @Path("/byNPI")
    @Produces("application/xml")
    public ProviderRepresentation getProviderByNPI(@QueryParam("id") String npi) {
        try {
        	long key = Long.parseLong(npi);
			ProviderDto providerDTO = providerService.getProviderByNPI(key);
			ProviderRepresentation providerRep = new ProviderRepresentation(providerDTO, uriInfo);
			return providerRep;
		} catch (ProviderServiceExn e) {
			throw new WebApplicationException();
		}
    }
     
    @POST
    @Consumes("application/xml")
    public Response addProvider(ProviderRepresentation providerRep){
    	try {
    		ProviderDto dto = providerDtoFactory.createProviderDto();
        	dto.setNpi(providerRep.getNpi());
        	dto.setName(providerRep.getName());
        	dto.setSpecialization(providerRep.getSpecialization());
			long id = providerService.addProvider(dto);
			UriBuilder ub = uriInfo.getAbsolutePathBuilder().path("{id}");
			URI url = ub.build(Long.toString(id));
			return Response.created(url).build();
		} catch (ProviderServiceExn e) {
			throw new WebApplicationException();
		}
    }
    
    @GET
    @Path("{id}")
    @Produces("application/xml")
    public ProviderRepresentation getProvider(@PathParam("id") String id) {
        try {
        	long key = Long.parseLong(id);
			ProviderDto providerDTO = providerService.getProvider(key);
			ProviderRepresentation providerRep = new ProviderRepresentation(providerDTO, uriInfo);
			return providerRep;
		} catch (ProviderServiceExn e) {
			throw new WebApplicationException();
		}
    }
    
    @POST
    @Path("/addtreatment")
    @Consumes("application/xml")
    public Response addTreatment(TreatmentRepresentation treatmentRep){
    	try {
    		TreatmentDto dto = null;
    		if (treatmentRep.getDrugtreatment() != null){
	    		dto = treatmentDtoFactory.createDrugTreatmentDto();
	    		dto.setPatient(Representation.getId(treatmentRep.getLinkPatient()));
	    		dto.setProvider(Representation.getId(treatmentRep.getLinkProvider()));
	    		dto.setDiagnosis(treatmentRep.getDiagnosis());
	    		dto.getDrugTreatment().setName(treatmentRep.getDrugtreatment().getName());
	    		dto.getDrugTreatment().setDosage(treatmentRep.getDrugtreatment().getDosage());
	    		dto.setProvider(providerService.getProvider(dto.getProvider()).getNpi());
	    		long id = providerService.addTreatmentForPat(dto, dto.getPatient(), dto.getProvider());
	    		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path("{id}");
	    		URI url = ub.build(Long.toString(id));
	    		return Response.created(url).build();
	    	} else if (treatmentRep.getSurgery() != null){
	    		dto = treatmentDtoFactory.createSurgeryDto();
	    		dto.setPatient(Representation.getId(treatmentRep.getLinkPatient()));
	    		dto.setProvider(Representation.getId(treatmentRep.getLinkProvider()));
	    		dto.setDiagnosis(treatmentRep.getDiagnosis());
	    		dto.getSurgery().setData(treatmentRep.getSurgery().getData());;
	    		dto.setProvider(providerService.getProvider(dto.getProvider()).getNpi());
	    		long id = providerService.addTreatmentForPat(dto, dto.getPatient(), this.getProvider(String.valueOf(dto.getProvider())).getNpi());
				UriBuilder ub = uriInfo.getAbsolutePathBuilder().path("{id}");
	    		URI url = ub.build(Long.toString(id));
	    		return Response.created(url).build();
	    	} else if (treatmentRep.getRadiology() != null){
	    		dto = treatmentDtoFactory.createRadiologyDto();
	    		dto.setPatient(Representation.getId(treatmentRep.getLinkPatient()));
	    		dto.setProvider(Representation.getId(treatmentRep.getLinkProvider()));
	    		dto.setDiagnosis(treatmentRep.getDiagnosis());
	    		dto.getRadiology().setDate(treatmentRep.getRadiology().getDate());
	    		dto.setProvider(providerService.getProvider(dto.getProvider()).getNpi());
	    		long id = providerService.addTreatmentForPat(dto, dto.getPatient(), this.getProvider(String.valueOf(dto.getProvider())).getNpi());
				UriBuilder ub = uriInfo.getAbsolutePathBuilder().path("{id}");
	    		URI url = ub.build(Long.toString(id));
	    		return Response.created(url).build();
	    	}
    	} catch (TreatmentNotFoundExn e) {
    		System.out.println(e.getMessage());
    		throw new WebApplicationException();
		} catch (PatientNotFoundExn e) {
			System.out.println(e.getMessage());
			throw new WebApplicationException();
		} catch (ProviderServiceExn e) {
			System.out.println(e.getMessage());
			throw new WebApplicationException();
		}
		return null;
    }
    
    @GET
    @Path("{id}/treatments/{tid}")
    @Produces("application/xml")
    public TreatmentRepresentation getProviderTreatment(@PathParam("id") String id, @PathParam("tid") String tid){
    	try {
			TreatmentDto dto = providerService.getTreatment(Long.parseLong(id), Long.parseLong(tid));
			TreatmentRepresentation treatmentRep = new TreatmentRepresentation(dto, uriInfo);
    		return treatmentRep;
    	} catch (NumberFormatException e) {
			throw new WebApplicationException();
		} catch (TreatmentNotFoundExn e) {
			throw new WebApplicationException();
		} catch (ProviderServiceExn e) {
			throw new WebApplicationException();
		}
    	
    }
    

}