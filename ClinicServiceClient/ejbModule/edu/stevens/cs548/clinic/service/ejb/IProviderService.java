package edu.stevens.cs548.clinic.service.ejb;

import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;

public interface IProviderService {

	public class ProviderServiceExn extends Exception{
		private static final long serialVersionUID = 1L;

		public ProviderServiceExn(String m){
			super(m);
		}
	}
	
	public class PatientNotFoundExn extends ProviderServiceExn {
		private static final long serialVersionUID = 1L;

		public PatientNotFoundExn(String m){
			super(m);
		}
	}
	
	public class TreatmentNotFoundExn extends ProviderServiceExn {
		private static final long serialVersionUID = 1L;

		public TreatmentNotFoundExn(String m){
			super(m);
		}
	}
	
	public long addProvider(ProviderDto prov) throws ProviderServiceExn;
	
	public ProviderDto getProvider(long id) throws ProviderServiceExn;
	
	public ProviderDto getProviderByNPI(long npi) throws ProviderServiceExn;
	
	public long addTreatmentForPat(TreatmentDto treatment, long pid, long npi) throws TreatmentNotFoundExn, PatientNotFoundExn, ProviderServiceExn;
	
	public TreatmentDto getTreatment(long id, long tid) throws TreatmentNotFoundExn, ProviderServiceExn;
		
	public String siteInfo();
	
}
