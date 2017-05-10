package edu.stevens.cs548.clinic.service.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.persistence.EntityManager;

import edu.stevens.cs548.clinic.domain.IProviderDAO.ProviderExn;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO.TreatmentExn;
import edu.stevens.cs548.clinic.domain.ITreatmentExporter;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientDAO;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.domain.ProviderDAO;
import edu.stevens.cs548.clinic.domain.ProviderFactory;
import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.domain.TreatmentFactory;
import edu.stevens.cs548.clinic.domain.IPatientDAO;
import edu.stevens.cs548.clinic.domain.IPatientDAO.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDAO;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentType;
import edu.stevens.cs548.clinic.service.dto.ObjectFactory;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyType;
import edu.stevens.cs548.clinic.service.dto.SurgeryType;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;


@Stateless(name="ProviderServiceBean")
public class ProviderService implements IProviderServiceLocal, IProviderServiceRemote{

	private Logger logger = Logger.getLogger(ProviderService.class.getCanonicalName());
	
	private ProviderFactory providerFactory;
	
	private IProviderDAO providerDAO;
	private IPatientDAO patientDAO;
	
	public ProviderService(){
		providerFactory = new ProviderFactory();
	}
	
	/*@PersistenceContext(unitName="ClinicDomain")
	private EntityManager em;*/
	@Inject @ClinicDomain EntityManager em;
	
	@PostConstruct
	private void initialize(){
		providerDAO = new ProviderDAO(em);
		patientDAO = new PatientDAO(em);
	}
	
	@Override
	public long addProvider(ProviderDto prov) throws ProviderServiceExn {
		try{
			Provider provider = providerFactory.createProvider(prov.getNpi(), prov.getName(), prov.getSpecialization());
			providerDAO.addProvider(provider);
			return provider.getId();
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		}
	}

	@Override
	public ProviderDto getProvider(long id) throws ProviderServiceExn {
		try{
			Provider prov = providerDAO.getProvider(id);
			return new ProviderDto(prov);
		} catch (ProviderExn e){
			throw new ProviderServiceExn(e.toString());
		}
	}

	@Override
	public ProviderDto getProviderByNPI(long npi) throws ProviderServiceExn {
		try{
			Provider prov = providerDAO.getProviderByNPI(npi);
			return new ProviderDto(prov);
		} catch (ProviderExn e){
			throw new ProviderServiceExn(e.toString());
		}
	}
	@Resource(mappedName="jms/TreatmentPool")
	private ConnectionFactory treatmentConnFactory;
	
	@Resource(mappedName="jms/Treatment")
	private Topic treatmentTopic;
	
	
	@Override
	public long addTreatmentForPat(TreatmentDto treatment, long pid, long npi)
			throws TreatmentNotFoundExn, PatientNotFoundExn, ProviderServiceExn, JMSException {
		Connection treatmentConn=null;
		try { 
			
			Patient pat = patientDAO.getPatient(pid);
			Provider prov = providerDAO.getProviderByNPI(npi);
			TreatmentFactory treatmentFactory = new TreatmentFactory();
			long idnumber = 0;
			treatmentConn=treatmentConnFactory.createConnection();
			Session session=treatmentConn.createSession(true, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer=session.createProducer(treatmentTopic);
			ObjectMessage message=session.createObjectMessage();
			TreatmentDto newt=new TreatmentDto();
			newt=treatment;
			if (npi != newt.getProvider()){
				throw new ProviderServiceExn("The provider can not add this treatment:npi = "+npi);
			}
			if (newt.getDrugTreatment() != null){
				Treatment t = treatmentFactory.createDrugTreatment(newt.getDiagnosis(), newt.getDrugTreatment().getName(), newt.getDrugTreatment().getDosage());
				t.setProvider(prov);
				message.setStringProperty("treatmentType", "DrugTreatmentRecord");
				idnumber=pat.addTreatment(t);
				newt.setId(idnumber);
				
			} else if (newt.getSurgery() != null){
				Treatment t = treatmentFactory.createSurgery(newt.getDiagnosis(), newt.getSurgery().getData());
				t.setProvider(prov);
				idnumber=pat.addTreatment(t);
				newt.setId(idnumber);
			} else if (treatment.getRadiology() != null) {
				Treatment t = treatmentFactory.createRadiology(newt.getDiagnosis(), newt.getRadiology().getDate());
				t.setProvider(prov);
				idnumber=pat.addTreatment(t);
				newt.setId(idnumber);
			} else {
				logger.severe("The treatment type is null!");
			}
			em.flush();
			message.setObject(newt);
			producer.send(message);
			return idnumber;
			
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		} catch (PatientExn e){
			throw new ProviderServiceExn(e.toString());
		} catch (JMSException e){
			logger.severe("JMS Error: "+e);
			throw new JMSException(e.toString());
		}
		finally{
			if(treatmentConn!=null){
				treatmentConn.close();
			}
		}
		
	}

	public class TreatmentExporter implements ITreatmentExporter<TreatmentDto> {
		
		private ObjectFactory factory = new ObjectFactory();
		
		@Override
		public TreatmentDto exportDrugTreatment(long tid, String diagnosis, String drug,
				float dosage, Provider prov, Patient patient) {
			TreatmentDto dto = factory.createTreatmentDto();
			dto.setDiagnosis(diagnosis);
			dto.setId(tid);
			dto.setPatient(patient.getId());
			dto.setProvider(prov.getId());
			DrugTreatmentType drugInfo = factory.createDrugTreatmentType();
			drugInfo.setDosage(dosage);
			drugInfo.setName(drug);
			dto.setDrugTreatment(drugInfo);
			return dto;
		}

		@Override
		public TreatmentDto exportRadiology(long tid, String diagnosis, List<Date> dates, Provider prov, Patient patient) {
			TreatmentDto dto = factory.createTreatmentDto();
			dto.setDiagnosis(diagnosis);
			dto.setId(tid);
			dto.setPatient(patient.getId());
			dto.setProvider(prov.getId());
			RadiologyType radiology = factory.createRadiologyType();
			radiology.setDate(dates);
			dto.setRadiology(radiology);
			return dto;
		}

		@Override
		public TreatmentDto exportSurgery(long tid, String diagnosis, Date date, Provider prov, Patient patient) {
			TreatmentDto dto = factory.createTreatmentDto();
			dto.setDiagnosis(diagnosis);
			dto.setId(tid);
			dto.setPatient(patient.getId());
			dto.setProvider(prov.getId());
			SurgeryType surgery = new SurgeryType();
			surgery.setData(date);
			dto.setSurgery(surgery);
			return dto;
		}
		
	}
	
	@Override
	public TreatmentDto getTreatment(long id, long tid) throws TreatmentNotFoundExn, ProviderServiceExn {
		// Export treatment DTO from patient aggregate
		try {
			Provider prov = providerDAO.getProvider(id);
			TreatmentExporter visitor = new TreatmentExporter();
			return prov.exportTreatment(tid, visitor);
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		} catch (TreatmentExn e) {
			throw new ProviderServiceExn(e.toString());
		}
	}
	
	@Resource(name="SiteInfo")
	private String siteInformation;
	

	@Override
	public String siteInfo() {
		return siteInformation;
	}

}
