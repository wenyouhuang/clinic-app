package edu.stevens.cs548.clinic.service.dto.util;

import edu.stevens.cs548.clinic.domain.DrugTreatment;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentType;
import edu.stevens.cs548.clinic.service.dto.ObjectFactory;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyType;
import edu.stevens.cs548.clinic.service.dto.SurgeryType;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

public class TreatmentDtoFactory {
	
	ObjectFactory factory;
	
	public TreatmentDtoFactory() {
		factory = new ObjectFactory();
	}
	
	public TreatmentDto createDrugTreatmentDto () {
		TreatmentDto treatDto = factory.createTreatmentDto();
		DrugTreatmentType drug = factory.createDrugTreatmentType();
		treatDto.setDrugTreatment(drug);
		return treatDto;
	}
	
	public TreatmentDto createSurgeryDto () {
		TreatmentDto treatDto = factory.createTreatmentDto();
		SurgeryType surgery = factory.createSurgeryType();
		treatDto.setSurgery(surgery);
		return treatDto;
	}
	
	public TreatmentDto createRadiologyDto () {
		TreatmentDto treatDto = factory.createTreatmentDto();
		RadiologyType radiology = factory.createRadiologyType();
		treatDto.setRadiology(radiology);
		return treatDto;
	}
	
	public PatientDto createTreatmentDto (DrugTreatment t) {
		/*
		 * TODO: Initialize the DTO from the drug treatment.
		 */
		return null;
	}
	
	/*
	 * TODO: Repeat for other treatments.
	 */

}
