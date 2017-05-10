package edu.stevens.cs548.clinic.domain;

import javax.persistence.EntityManager;

public class TreatmentDAO implements ITreatmentDAO {
	
	public TreatmentDAO (EntityManager em) {
		this.em = em;
	}
	
	private EntityManager em;

	@SuppressWarnings("unused")
	@Override
	public Treatment getTreatment(long id) throws TreatmentExn {
		Treatment t = em.find(Treatment.class, id);
		System.out.println(id+t.getTreatmentType());
		if (t == null) {
			throw new TreatmentExn("Missing treatment: id = " + id);
		} else {
			return t;
		}
	}

	@Override
	public long addTreatment(Treatment t) {
		em.persist(t);
		em.flush();
		return t.getId();
	}
	
	@Override
	public void deleteTreatment(Treatment t) {
		em.remove(t);
	}
	
}
