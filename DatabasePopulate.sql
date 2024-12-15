-- Insert clinics
INSERT INTO Clinics (id, name) VALUES
('A', 'Clinic A'),
('B', 'Clinic B'),
('C', 'Clinic C'),
('D', 'Clinic D');

-- Insert specialties for each clinic
INSERT INTO Specialties (name, clinic_id) VALUES
('General Practice', 'A'),
('Pediatrics', 'A'),
('Cardiology', 'A'),
('Gynecology', 'A'),
('General Practice', 'B'),
('Gynecology', 'B'),
('Hematology', 'B'),
('Tropical Medicine', 'C'),
('Neurology', 'C'),
('Ophthalmology', 'D'),
('Oncology', 'D'),
('Otorhinolaryngology', 'D');

-- Clinic A
INSERT INTO Doctors (id, name, specialty_name, clinic_id) VALUES
('DOCA1', 'Doctor A1', 'General Practice', 'A'),
('DOCA2', 'Doctor A2', 'General Practice', 'A'),
('DOCA3', 'Doctor A3', 'General Practice', 'A'),
('DOCA4', 'Doctor A4', 'General Practice', 'A'),
('DOCA5', 'Doctor A5', 'General Practice', 'A'),
('DOCA6', 'Doctor A6', 'General Practice', 'A'),
('DOCA7', 'Doctor A7', 'General Practice', 'A'),
('DOCA8', 'Doctor A8', 'General Practice', 'A'),
('DOCA9', 'Doctor A9', 'General Practice', 'A'),
('DOCA10', 'Doctor A10', 'General Practice', 'A'),
('DOCA11', 'Doctor A11', 'Pediatrics', 'A'),
('DOCA12', 'Doctor A12', 'Pediatrics', 'A'),
('DOCA13', 'Doctor A13', 'Pediatrics', 'A'),
('DOCA14', 'Doctor A14', 'Pediatrics', 'A'),
('DOCA15', 'Doctor A15', 'Pediatrics', 'A'),
('DOCA16', 'Doctor A16', 'Cardiology', 'A'),
('DOCA17', 'Doctor A17', 'Gynecology', 'A'),
('DOCA18', 'Doctor A18', 'Gynecology', 'A');

-- Clinic B
INSERT INTO Doctors (id, name, specialty_name, clinic_id) VALUES
('DOCB1', 'Doctor B1', 'General Practice', 'B'),
('DOCB2', 'Doctor B2', 'General Practice', 'B'),
('DOCB3', 'Doctor B3', 'General Practice', 'B'),
('DOCB4', 'Doctor B4', 'General Practice', 'B'),
('DOCB5', 'Doctor B5', 'General Practice', 'B'),
('DOCB6', 'Doctor B6', 'Gynecology', 'B'),
('DOCB7', 'Doctor B7', 'Hematology', 'B');

-- Clinic C
INSERT INTO Doctors (id, name, specialty_name, clinic_id) VALUES
('DOCC1', 'Doctor C1', 'Tropical Medicine', 'C'),
('DOCC2', 'Doctor C2', 'Neurology', 'C');

-- Clinic D
INSERT INTO Doctors (id, name, specialty_name, clinic_id) VALUES
('DOCD1', 'Doctor D1', 'Ophthalmology', 'D'),
('DOCD2', 'Doctor D2', 'Ophthalmology', 'D'),
('DOCD3', 'Doctor D3', 'Oncology', 'D'),
('DOCD4', 'Doctor D4', 'Oncology', 'D'),
('DOCD5', 'Doctor D5', 'Oncology', 'D'),
('DOCD6', 'Doctor D6', 'Otorhinolaryngology', 'D');