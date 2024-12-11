-- Drop existing tables (if necessary)
DROP TABLE IF EXISTS Appointments;
DROP TABLE IF EXISTS Doctors;
DROP TABLE IF EXISTS Specialties;
DROP TABLE IF EXISTS Clinics;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Clinics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE Specialties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    clinic_id INT,
    FOREIGN KEY (clinic_id) REFERENCES Clinics(id)
);

CREATE TABLE Doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty_id INT NOT NULL,
    clinic_id INT NOT NULL,
    FOREIGN KEY (specialty_id) REFERENCES Specialties(id),
    FOREIGN KEY (clinic_id) REFERENCES Clinics(id)
);

CREATE TABLE Appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    specialty_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_slot INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (specialty_id) REFERENCES Specialties(id),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(id),
    UNIQUE (doctor_id, appointment_date, appointment_slot)
);
