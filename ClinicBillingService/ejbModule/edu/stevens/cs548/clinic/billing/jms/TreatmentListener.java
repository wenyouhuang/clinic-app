package edu.stevens.cs548.clinic.billing.jms;

import java.util.Date;
//import java.util.Date;
import java.util.Random;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message; 
import javax.jms.MessageListener; 
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.stevens.cs548.clinic.billing.service.BillingDtoFactory;
import edu.stevens.cs548.clinic.billing.service.BillingService;
import edu.stevens.cs548.clinic.billing.service.IBillingService.BillingDTO;
import edu.stevens.cs548.clinic.billing.service.IBillingServiceLocal;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO.TreatmentExn;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

/**
 * Message-Driven Bean implementation class for: TreatmentListener
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(
				propertyName = "destination", propertyValue = "Treatment"),
				@ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Topic")
		}, 
		mappedName = "jms/Treatment")
public class TreatmentListener implements MessageListener {

    /**
     * Default constructor. 
     */
    public TreatmentListener() {
        // TODO Auto-generated constructor stub
    	billtodofactory=new BillingDtoFactory();
    }
	@PersistenceContext(unitName="ClinicDomain")
    private EntityManager em;
	/**
     * @see MessageListener#onMessage(Message)
     */
	@Inject
	private IBillingServiceLocal billservice;
	private BillingDtoFactory billtodofactory;
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
    	ObjectMessage objmessage=(ObjectMessage)message;
    	try {
			TreatmentDto treatment=(TreatmentDto)objmessage.getObject();
			BillingDTO billdto=billtodofactory.createBillingDTO();
			Random generator= new Random();
			
			float amount=generator.nextFloat()*500;
			Date now=new Date();
			
			billdto.setDate(now);
			billdto.setAmount(amount);
			billdto.setDescription(treatment.getDiagnosis());
			billdto.setTreatmentId(treatment.getId());
			
			billservice.addBillingRecord(billdto);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TreatmentExn e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
