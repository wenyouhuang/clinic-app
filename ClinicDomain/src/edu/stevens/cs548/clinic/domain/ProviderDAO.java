package edu.stevens.cs548.clinic.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;


@SuppressWarnings("unused")
@NamedQueries({
	@NamedQuery(
			name="SearchProviderByNPI",
			query="select prov from Provider prov where prov.npi = :npi"),
	@NamedQuery(
			name="RemoveAllProviders",
			query="delete from Provider prov")
})
public class ProviderDAO implements IProviderDAO {

	private EntityManager em;
	private TreatmentDAO treatmentDAO;
	
	public ProviderDAO (EntityManager em) {
		this.em = em;
		this.treatmentDAO = new TreatmentDAO(em);
	}
	
	//5a
	public Provider getProvider(long id) throws ProviderExn {
		// TODO Auto-generated method stub
		Provider prov = em.find(Provider.class, id);
		if (prov == null){
			throw new ProviderExn("Provider not found: primary key = " + id);
		} else {
			return prov;
		}
	}
	
	//5b
	public Provider getProviderByNPI(long npi) throws ProviderExn{
		// TODO Auto-generated method stub
		TypedQuery<Provider> query = em.createNamedQuery("SearchProviderByNPI", Provider.class)
				.setParameter("npi", npi);
		List<Provider> providers = query.getResultList();
		if (providers.size() > 1){
			throw new ProviderExn("Duplicate patient records: National Provider Identifier = " + npi);
		} else if (providers.size() < 1) {
			throw new ProviderExn("Provider not found: National Provider Identifier = " + npi);
		} else {
			Provider prov = providers.get(0);
			return prov;
		}
	}

	//4
	public void addProvider(Provider prov) throws ProviderExn {
		// TODO Auto-generated method stub
		long npi = prov.getNpi();
		TypedQuery<Provider> query = em.createNamedQuery("SearchProviderByNPI", Provider.class)
				.setParameter("npi", npi);
		List<Provider> providers = query.getResultList();
		if (providers.size() < 1) {
			em.persist(prov);
			prov.setTreatmentDAO(new TreatmentDAO(em));
		} else {
			Provider prov2 = providers.get(0);
			throw new ProviderExn("Insertion: Provider with National Provider Identifier(" + npi
					+") already exists.\n** Note:"+prov2.getName());
		}
	}

	public void deleteProvider(Provider prov) throws ProviderExn {
		// TODO Auto-generated method stub
		em.remove(prov);
	}
	
}
