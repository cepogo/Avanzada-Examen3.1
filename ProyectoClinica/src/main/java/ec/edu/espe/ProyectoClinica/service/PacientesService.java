package ec.edu.espe.ProyectoClinica.service;

import ec.edu.espe.ProyectoClinica.api.dto.PacientesDTO;
import ec.edu.espe.ProyectoClinica.entity.PacientesEntity;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PacientesService {
    private final PacientesRepository pacientesRepository;
    private String msgError;

    public PacientesService(PacientesRepository pacientesRepository) {
        this.pacientesRepository = pacientesRepository;
    }

    private PacientesDTO mapEntityToDTO(PacientesEntity entity) {
        PacientesDTO dto = new PacientesDTO();
        dto.setId(entity.getPacienteId());
        dto.setNombre(entity.getPacienteNombre());
        dto.setApellido(entity.getPacienteApellido());
        dto.setFechaNacimiento(entity.getPacientefechaNacimiento());
        dto.setEmail(entity.getPacienteEmail());
        return dto;
    }

    private PacientesEntity mapDTOToEntity(PacientesDTO dto) {
        PacientesEntity entity = new PacientesEntity();
        if (dto.getId() != null) {
            entity.setPacienteId(dto.getId());
        }
        entity.setPacienteNombre(dto.getNombre());
        entity.setPacienteApellido(dto.getApellido());
        entity.setPacientefechaNacimiento(dto.getFechaNacimiento());
        entity.setPacienteEmail(dto.getEmail());
        return entity;
    }

    public List<PacientesDTO> getAllPacientes() throws DocumentNotFoundException {
        try {
            List<PacientesEntity> pacientesEntities = pacientesRepository.findAll();
            List<PacientesDTO> pacientesDTOS = new ArrayList<>();

            for (PacientesEntity entity : pacientesEntities) {
                pacientesDTOS.add(mapEntityToDTO(entity));
            }
            return pacientesDTOS;
        } catch (Exception exception) {
            throw new DocumentNotFoundException("No hay pacientes registrados en el sistema", null);
        }
    }

    public void create(PacientesDTO pacientesDTO) throws InsertException {
        try {
            // Validar campos requeridos
            if (pacientesDTO.getNombre() == null || pacientesDTO.getNombre().trim().isEmpty()) {
                throw new InsertException("Debe ingresar el nombre del paciente", null);
            }
            if (pacientesDTO.getApellido() == null || pacientesDTO.getApellido().trim().isEmpty()) {
                throw new InsertException("Debe ingresar el apellido del paciente", null);
            }
            if (pacientesDTO.getEmail() == null || pacientesDTO.getEmail().trim().isEmpty()) {
                throw new InsertException("Debe ingresar el correo electrónico del paciente", null);
            }
            if (pacientesDTO.getFechaNacimiento() == null) {
                throw new InsertException("Debe ingresar la fecha de nacimiento del paciente", null);
            }

            // Validar duplicados de nombre y apellido
            if (pacientesRepository.existsByPacienteNombreAndPacienteApellido(
                    pacientesDTO.getNombre(), pacientesDTO.getApellido())) {
                throw new InsertException(
                    String.format("El paciente %s %s ya se encuentra registrado en el sistema", 
                    pacientesDTO.getNombre(), pacientesDTO.getApellido()),
                    null);
            }

            // Validar duplicados de email
            if (pacientesRepository.existsByPacienteEmail(pacientesDTO.getEmail())) {
                throw new InsertException(
                    String.format("El correo electrónico %s ya se encuentra registrado en el sistema", 
                    pacientesDTO.getEmail()),
                    null);
            }

            // Validar que la fecha de nacimiento esté dentro del rango permitido
            java.util.Calendar calMin = java.util.Calendar.getInstance();
            calMin.set(1995, 0, 1); // 1995-01-01
            java.util.Date fechaMinima = calMin.getTime();

            java.util.Calendar calMax = java.util.Calendar.getInstance();
            java.util.Date fechaMaxima = calMax.getTime();

            if (pacientesDTO.getFechaNacimiento().before(fechaMinima) || 
                pacientesDTO.getFechaNacimiento().after(fechaMaxima)) {
                throw new InsertException(
                    "La fecha de nacimiento debe ser desde el 1 de enero de 1995 hasta la fecha actual",
                    null);
            }

            PacientesEntity entity = mapDTOToEntity(pacientesDTO);
            this.pacientesRepository.save(entity);
        } catch (InsertException e) {
            throw e;
        } catch (Exception exception) {
            throw new InsertException("Error en el sistema. No se pudo completar el registro del paciente", null);
        }
    }

    public void update(PacientesDTO pacientesDTO) throws UpdateException {
        try {
            if (pacientesDTO.getId() == null) {
                throw new UpdateException("Debe especificar el ID del paciente que desea actualizar", null);
            }

            Optional<PacientesEntity> optionalEntity = this.pacientesRepository.findById(pacientesDTO.getId());
            if (optionalEntity.isEmpty()) {
                throw new UpdateException("El paciente que intenta actualizar no existe en el sistema", null);
            }

            PacientesEntity existingPaciente = optionalEntity.get();

            // Validar duplicados de nombre y apellido solo si son diferentes a los actuales
            if ((pacientesDTO.getNombre() != null && !existingPaciente.getPacienteNombre().equals(pacientesDTO.getNombre())) ||
                (pacientesDTO.getApellido() != null && !existingPaciente.getPacienteApellido().equals(pacientesDTO.getApellido()))) {
                if (pacientesRepository.existsByPacienteNombreAndPacienteApellido(
                        pacientesDTO.getNombre() != null ? pacientesDTO.getNombre() : existingPaciente.getPacienteNombre(),
                        pacientesDTO.getApellido() != null ? pacientesDTO.getApellido() : existingPaciente.getPacienteApellido())) {
                    throw new UpdateException(
                        String.format("El paciente %s %s ya se encuentra registrado en el sistema",
                        pacientesDTO.getNombre() != null ? pacientesDTO.getNombre() : existingPaciente.getPacienteNombre(),
                        pacientesDTO.getApellido() != null ? pacientesDTO.getApellido() : existingPaciente.getPacienteApellido()),
                        null);
                }
            }

            // Validar duplicados de email solo si es diferente al actual
            if (pacientesDTO.getEmail() != null && !existingPaciente.getPacienteEmail().equals(pacientesDTO.getEmail())) {
                if (pacientesRepository.existsByPacienteEmail(pacientesDTO.getEmail())) {
                    throw new UpdateException(
                        String.format("El correo electrónico %s ya se encuentra registrado en el sistema",
                        pacientesDTO.getEmail()),
                        null);
                }
            }

            // Validar que la fecha de nacimiento esté dentro del rango permitido si se está actualizando
            if (pacientesDTO.getFechaNacimiento() != null) {
                java.util.Calendar calMin = java.util.Calendar.getInstance();
                calMin.set(1995, 0, 1); // 1995-01-01
                java.util.Date fechaMinima = calMin.getTime();

                java.util.Calendar calMax = java.util.Calendar.getInstance();
                java.util.Date fechaMaxima = calMax.getTime();

                if (pacientesDTO.getFechaNacimiento().before(fechaMinima) || 
                    pacientesDTO.getFechaNacimiento().after(fechaMaxima)) {
                    throw new UpdateException(
                        "La fecha de nacimiento debe ser desde el 1 de enero de 1995 hasta la fecha actual",
                        null);
                }
            }

            if (pacientesDTO.getNombre() != null) existingPaciente.setPacienteNombre(pacientesDTO.getNombre());
            if (pacientesDTO.getApellido() != null) existingPaciente.setPacienteApellido(pacientesDTO.getApellido());
            if (pacientesDTO.getFechaNacimiento() != null) existingPaciente.setPacientefechaNacimiento(pacientesDTO.getFechaNacimiento());
            if (pacientesDTO.getEmail() != null) existingPaciente.setPacienteEmail(pacientesDTO.getEmail());

            this.pacientesRepository.save(existingPaciente);
        } catch (UpdateException e) {
            throw e;
        } catch (Exception exception) {
            throw new UpdateException("Error en el sistema. No se pudo completar la actualización del paciente", null);
        }
    }

    public void delete(Integer id) throws DeleteException {
        try {
            Optional<PacientesEntity> optionalEntity = this.pacientesRepository.findById(id);
            if (optionalEntity.isEmpty()) {
                throw new DeleteException("El paciente que intenta eliminar no existe en el sistema", null);
            }

            PacientesEntity paciente = optionalEntity.get();
            if (paciente.getCitas() != null && !paciente.getCitas().isEmpty()) {
                throw new DeleteException(
                    "No se puede eliminar el paciente porque tiene citas pendientes. Debe cancelar las citas primero",
                    null);
            }

            this.pacientesRepository.delete(paciente);
        } catch (DeleteException de) {
            throw de;
        } catch (Exception exception) {
            throw new DeleteException("Error en el sistema. No se pudo completar la eliminación del paciente", null);
        }
    }
}
