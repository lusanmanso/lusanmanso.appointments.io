-- Drop existing tables (if necessary)
DROP TABLE IF EXISTS Appointments;
DROP TABLE IF EXISTS Doctors;
DROP TABLE IF EXISTS Specialties;
DROP TABLE IF EXISTS Clinics;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    email VARCHAR(100) PRIMARY KEY, -- Use email as ID
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Clinics (
    id CHAR(1) PRIMARY KEY, -- A, B, C, D
    name VARCHAR(50) NOT NULL
);

CREATE TABLE Specialties (
    name VARCHAR(100) PRIMARY KEY, -- Use specialty name as ID
    clinic_id CHAR(1) NOT NULL,
    FOREIGN KEY (clinic_id) REFERENCES Clinics(id)
);

CREATE TABLE Doctors (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    specialty_name VARCHAR(50) NOT NULL, 
    clinic_id CHAR(1) NOT NULL,
    FOREIGN KEY (specialty_name) REFERENCES Specialties(name),
    FOREIGN KEY (clinic_id) REFERENCES Clinics(id)
);

CREATE TABLE Appointments (
    id VARCHAR(10) PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL,
    specialty_name VARCHAR(50)NOT NULL,
    doctor_id VARCHAR(10) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_slot INT NOT NULL,
    FOREIGN KEY (user_email) REFERENCES Users(id),
    FOREIGN KEY (specialty_name) REFERENCES Specialties(name),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(id),
);
