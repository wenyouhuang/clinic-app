package edu.stevens.cs548.clinic.domain;

public class ProviderFactory implements IProviderFactory {

	public Provider createProvider(long npi, String name, String specilization) {
		// TODO Auto-generated method stub
		Provider prov = new Provider();
		prov.setNpi(npi);
		prov.setName(name);
		prov.setSpecialization(specilization);
		return prov;
	}

}
