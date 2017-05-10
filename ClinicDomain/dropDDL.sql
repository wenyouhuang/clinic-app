ALTER TABLE TREATMENT DROP CONSTRAINT FK_TREATMENT_provider_fk
ALTER TABLE TREATMENT DROP CONSTRAINT FK_TREATMENT_patient_fk
ALTER TABLE DrugTreatment DROP CONSTRAINT FK_DrugTreatment_ID
ALTER TABLE Radiology DROP CONSTRAINT FK_Radiology_ID
ALTER TABLE Surgery DROP CONSTRAINT FK_Surgery_ID
ALTER TABLE BILLINGRECORD DROP CONSTRAINT FK_BILLINGRECORD_treatment_id
ALTER TABLE DRUGTREATMENTRECORD DROP CONSTRAINT FK_DRUGTREATMENTRECORD_SUBJECT_ID
DROP TABLE PATIENT CASCADE
DROP TABLE TREATMENT CASCADE
DROP TABLE DrugTreatment CASCADE
DROP TABLE PROVIDER CASCADE
DROP TABLE Radiology CASCADE
DROP TABLE Surgery CASCADE
DROP TABLE BILLINGRECORD CASCADE
DROP TABLE DRUGTREATMENTRECORD CASCADE
DROP TABLE SUBJECT CASCADE
DELETE FROM SEQUENCE WHERE SEQ_NAME = 'SEQ_GEN'
