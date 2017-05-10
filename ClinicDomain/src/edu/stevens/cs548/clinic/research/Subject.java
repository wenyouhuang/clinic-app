package edu.stevens.cs548.clinic.research;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Entity implementation class for Entity: Subject
 *
 */
@Entity

public class Subject implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public Subject() {
		super();
	}
	/*
	* This will be same as patient id in Clinic database */
	@Id 
	private long id;
	 

	/*
	 * Anonymize patient (randomly generated when assigned) */
	private long subjectId;
	@OneToMany(mappedBy = "subject")
	private Collection<DrugTreatmentRecord> treatments;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(long subjectId) {
		this.subjectId = subjectId;
	}
	public Collection<DrugTreatmentRecord> getTreatments() {
		return treatments;
	}
	public void setTreatments(Collection<DrugTreatmentRecord> treatments) {
		this.treatments = treatments;
	}
}
