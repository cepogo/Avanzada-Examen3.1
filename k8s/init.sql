CREATE DATABASE IF NOT EXISTS ProyectoClinica;
USE ProyectoClinica;

-- Crear índices únicos
ALTER TABLE pacientes ADD UNIQUE INDEX IF NOT EXISTS idx_email (paciente_email);
ALTER TABLE pacientes ADD UNIQUE INDEX IF NOT EXISTS idx_nombre_apellido (paciente_nombre, paciente_apellido);
ALTER TABLE medicos ADD UNIQUE INDEX IF NOT EXISTS idx_nombre_apellido (medico_nombre, medico_apellido);
ALTER TABLE consultorios ADD UNIQUE INDEX IF NOT EXISTS idx_piso_numero (consultorio_piso, consultorio_numero);

-- Configurar el charset y collation
ALTER DATABASE ProyectoClinica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; 