-- Limpiar datos existentes
DELETE FROM citas;
DELETE FROM pacientes;
DELETE FROM medicos;
DELETE FROM consultorios;

-- Insertar consultorio de prueba
INSERT INTO consultorios (id, numero, piso) VALUES (1, '101', 1);

-- Insertar datos de prueba para pacientes
INSERT INTO pacientes (id, nombre, apellido, fecha_nacimiento, email)
VALUES 
(1, 'Juan', 'Pérez', '1990-01-01', 'juan.perez@test.com'),
(2, 'María', 'García', '1985-05-15', 'maria.garcia@test.com');

-- Insertar datos de prueba para médicos
INSERT INTO medicos (id, nombre, apellido, especialidad)
VALUES 
(1, 'Dr. Carlos', 'Rodríguez', 'Cardiología'),
(2, 'Dra. Ana', 'Martínez', 'Pediatría');

-- Insertar datos de prueba para citas
INSERT INTO citas (id, paciente_id, medico_id, fecha, hora, consultorio_id)
VALUES 
(1, 1, 1, '2024-03-15', '09:00:00', 1),
(2, 2, 2, '2024-03-16', '10:00:00', 1); 