package edu.stevens.cs548.clinic.research.jms;

//import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import edu.stevens.cs548.clinic.billing.service.IResearchService.DrugTreatmentDTO;
import edu.stevens.cs548.clinic.billing.service.IResearchServiceLocal;
//import edu.stevens.cs548.clinic.research.domain.ResearchFactory;
import edu.stevens.cs548.clinic.research.service.DrugTreatmentDtoFactory;
import edu.stevens.cs548.clinic.research.service.ResearchService;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

/**
 * Message-Driven Bean implementation class for: DrugTreatmentListener
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "Treatment"), 
				@ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
				@ActivationConfigProperty(
				propertyName = "messageSelector", propertyValue = "treatmentType='DrugTreatmentRecord'")
		
		}, 
		mappedName = "jms/Treatment")
public class DrugTreatmentListener implements MessageListener {

    /**
     * Default constructor. 
     */
    public DrugTreatmentListener() {
        // TODO Auto-generated constructor stub
//    	researchservice=new ResearchService();
    	drugtdofactory=new DrugTreatmentDtoFactory();
    }
    @Inject
    private IResearchServiceLocal researchservice;
//	private ResearchFactory researchfactory;
	private DrugTreatmentDtoFactory drugtdofactory;
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
    	ObjectMessage objmessage=(ObjectMessage)message;
    	try {
			
			TreatmentDto treatment=(TreatmentDto)objmessage.getObject();
			if(treatment.getDrugTreatment().getName()==null){
				return;
			}
			
			DrugTreatmentDTO drugtreatment=drugtdofactory.createDrugTreatmentDTO();
			drugtreatment.setPatientId(treatment.getPatient());
			drugtreatment.setDosage(treatment.getDrugTreatment().getDosage());
			drugtreatment.setDrugName(treatment.getDrugTreatment().getName());
			drugtreatment.setTreatmentId(treatment.getId());
			researchservice.addDrugTreatmentRecord(drugtreatment);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}
