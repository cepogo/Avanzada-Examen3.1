CREATE DATABASE IF NOT EXISTS proyectoclinica;
USE proyectoclinica;

CREATE TABLE Pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE Medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL
);

CREATE TABLE Consultorios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    piso INT NOT NULL
);

CREATE TABLE Citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    consultorio_id INT NOT NULL,
    FOREIGN KEY (paciente_id) REFERENCES Pacientes(id) ON DELETE CASCADE,
    FOREIGN KEY (medico_id) REFERENCES Medicos(id) ON DELETE CASCADE,
    FOREIGN KEY (consultorio_id) REFERENCES Consultorios(id) ON DELETE CASCADE
);