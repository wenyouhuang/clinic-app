package edu.stevens.cs548.clinic.billing;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import edu.stevens.cs548.clinic.domain.Treatment;

import static javax.persistence.GenerationType.IDENTITY;
//import static javax.persistence.TemporalType.DATE;
import org.eclipse.persistence.annotations.JoinFetch;
import static org.eclipse.persistence.annotations.JoinFetchType.INNER;
import static javax.persistence.CascadeType.PERSIST;
 
/**
 * Entity implementation class for Entity: BillingRecord
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(
		name = "DeleteBillingRecords", 
		query = "delete from BillingRecord p"),
	@NamedQuery(
			name = "SearchBillingRecords", 
			query = "select p from BillingRecord p")
})
public class BillingRecord implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public BillingRecord() {
		super();
	}
	@Id
	@GeneratedValue
	private long id;
	
	private String description;
	
	@Temporal(TemporalType.DATE)
	private Date date; 	
	
	private float amount;
	
	@OneToOne
	@JoinColumn(name = "treatment_id", referencedColumnName = "id")
	private Treatment treatment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}
   
}
