package edu.stevens.cs548.clinic.domain;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;


@SuppressWarnings("unused")
@NamedQueries({
	@NamedQuery(
		name="SearchPatientByPatientID",
		query="select p from Patient p where p.patientId = :pid"),
	@NamedQuery(
		name="CountPatientByPatientID",
		query="select count(p) from Patient p where p.patientId = :pid"),
	@NamedQuery(
		name = "RemoveAllPatients", 
		query = "delete from Patient p")
})

public class PatientDAO implements IPatientDAO {

	private EntityManager em;
	private TreatmentDAO treatmentDAO;
	
	public PatientDAO(EntityManager em) {
		this.em = em;
		this.treatmentDAO = new TreatmentDAO(em);
	}

	private Logger logger = Logger.getLogger(PatientDAO.class.getCanonicalName());

	//1
	@Override
	public long addPatient(Patient patient) throws PatientExn {
		long pid = patient.getPatientId();
		Query query = em.createNamedQuery("CountPatientByPatientID",Patient.class).setParameter("pid", pid);
		Long numExisting = (Long) query.getSingleResult();
		if (numExisting < 1) {
			// TODO add to database (and sync with database to generate primary key)
			// Don't forget to initialize the patient aggregate with a treatment DAO
			em.persist(patient);
			patient.setTreatmentDAO(new TreatmentDAO(em));
			//throw new IllegalStateException("Unimplemented");
			return pid;
		} else {
			throw new PatientExn("Insertion: Patient with patient id (" + pid + ") already exists.");
		}
	}

	//2
	@Override
	public Patient getPatient(long id) throws PatientExn {
		// TODO retrieve patient using primary key
		Patient patient = em.find(Patient.class, id);
		if (patient == null){
			throw new PatientExn("Patient not found: primary key = " + id);
		} else {
			patient.setTreatmentDAO(new TreatmentDAO(em));
			return patient;
		}
	}

	//3
	@Override
	public Patient getPatientByPatientId(long pid) throws PatientExn {
		// TODO retrieve patient using query on patient id (secondary key)
		TypedQuery<Patient> query = 
				em.createNamedQuery("SearchPatientByPatientID", Patient.class)
				.setParameter("pid", pid);
		List<Patient> patients = query.getResultList();
		if (patients.size() > 1)
			throw new PatientExn("Duplicate patient records: patient id = " + pid);
		else if (patients.size() < 1)
			throw new PatientExn("Patient not found: patient id = " + pid);
		else {
			Patient p = patients.get(0);
			p.setTreatmentDAO(new TreatmentDAO(em));
			return p; 
		}
	}
	
	@Override
	public void deletePatients(){
		/* test logic 
		 * delete all treatments and providers*/
		Query deleteDrugTreatment = em.createNamedQuery("RemoveAllDrugTreatments", DrugTreatment.class);
		deleteDrugTreatment.executeUpdate();
		Query deleteSurgery = em.createNamedQuery("RemoveAllSurgery", Surgery.class);
		deleteSurgery.executeUpdate();
		Query deleteRadiology = em.createNamedQuery("RemoveAllRadiology", Radiology.class);
		deleteRadiology.executeUpdate();
		Query deleteTreatment = em.createNamedQuery("RemoveAllTreatments", Treatment.class);
		deleteTreatment.executeUpdate();
		Query deleteProvider = em.createNamedQuery("RemoveAllProviders", Provider.class);
		deleteProvider.executeUpdate();
		/* test logic */
		
		Query update = em.createNamedQuery("RemoveAllPatients", Patient.class);
		update.executeUpdate();
	}

}
