-- Insert clinics
INSERT INTO Clinics (id, name) VALUES
(1, 'Clinic A'),
(2, 'Clinic B'),
(3, 'Clinic C'),
(4, 'Clinic D');

-- Insert specialties for each clinic
INSERT INTO Specialties (id, name, clinic_id) VALUES
(1, 'General Practice', 1),
(2, 'Pediatrics', 1),
(3, 'Cardiology', 1),
(4, 'Gynecology', 1),
(5, 'General Practice', 2),
(6, 'Gynecology', 2),
(7, 'Hematology', 2),
(8, 'Tropical Medicine', 3),
(9, 'Neurology', 3),
(10, 'Ophthalmology', 4),
(11, 'Oncology', 4),
(12, 'Otorhinolaryngology', 4);

-- Clinic A
INSERT INTO Doctors (name, specialty_id, clinic_id) VALUES
('Doctor A1', 1, 1), ('Doctor A2', 1, 1), ('Doctor A3', 1, 1),
('Doctor A4', 1, 1), ('Doctor A5', 1, 1), ('Doctor A6', 1, 1),
('Doctor A7', 1, 1), ('Doctor A8', 1, 1), ('Doctor A9', 1, 1),
('Doctor A10', 1, 1), -- General Practice (10 doctors)
('Doctor A11', 2, 1), ('Doctor A12', 2, 1), ('Doctor A13', 2, 1),
('Doctor A14', 2, 1), ('Doctor A15', 2, 1), -- Pediatrics (5 doctors)
('Doctor A16', 3, 1), -- Cardiology (1 doctor)
('Doctor A17', 4, 1), ('Doctor A18', 4, 1); -- Gynecology (2 doctors)

-- Clinic B
INSERT INTO Doctors (name, specialty_id, clinic_id) VALUES
('Doctor B1', 5, 2), ('Doctor B2', 5, 2), ('Doctor B3', 5, 2),
('Doctor B4', 5, 2), ('Doctor B5', 5, 2), -- General Practice (5 doctors)
('Doctor B6', 6, 2), -- Gynecology (1 doctor)
('Doctor B7', 7, 2); -- Hematology (1 doctor)

-- Clinic C
INSERT INTO Doctors (name, specialty_id, clinic_id) VALUES
('Doctor C1', 8, 3), -- Tropical Medicine (1 doctor)
('Doctor C2', 9, 3); -- Neurology (1 doctor)

-- Clinic D
INSERT INTO Doctors (name, specialty_id, clinic_id) VALUES
('Doctor D1', 10, 4), ('Doctor D2', 10, 4), -- Ophthalmology (2 doctors)
('Doctor D3', 11, 4), ('Doctor D4', 11, 4), ('Doctor D5', 11, 4), -- Oncology (3 doctors)
('Doctor D6', 12, 4); -- Otorhinolaryngology (1 doctor)