package edu.stevens.cs548.clinic.research;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: DrugTreatmentRecord
 *
 */
@Entity 
@NamedQueries({
	@NamedQuery(
		name = "DeleteDrugTreatmentRecords", 
		query = "delete from DrugTreatmentRecord p"),
	@NamedQuery(
			name = "SearchDrugTreatmentRecords", 
			query = "select p from DrugTreatmentRecord p")
})
public class DrugTreatmentRecord implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public DrugTreatmentRecord() {
		super();
	}
	@Id @GeneratedValue
	private long id;
	@Temporal(TemporalType.DATE) 
	private Date date;
	private String drugName; private float dosage;
	@ManyToOne
	private Subject subject;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public float getDosage() {
		return dosage;
	}
	public void setDosage(float dosage) {
		this.dosage = dosage;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
   
}
