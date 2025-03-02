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
            throw new DocumentNotFoundException("No se encontraron los pacientes", PacientesEntity.class.getName());
        }
    }

    public void create(PacientesDTO pacientesDTO) throws InsertException {
        try {
            if (pacientesDTO.getFechaNacimiento() == null) {
                throw new InsertException("La fecha de nacimiento es requerida", PacientesEntity.class.getName());
            }
            PacientesEntity entity = mapDTOToEntity(pacientesDTO);
            this.pacientesRepository.save(entity);
        } catch (Exception exception) {
            throw new InsertException("Error creando paciente: " + exception.getMessage(), PacientesEntity.class.getName());
        }
    }

    public void update(PacientesDTO pacientesDTO) throws UpdateException {
        try {
            if (pacientesDTO.getId() == null) {
                throw new UpdateException("El ID del paciente es requerido", PacientesEntity.class.getName());
            }

            Optional<PacientesEntity> optionalEntity = this.pacientesRepository.findById(pacientesDTO.getId());
            if (optionalEntity.isEmpty()) {
                throw new UpdateException("Paciente no encontrado", PacientesEntity.class.getName());
            }

            PacientesEntity entity = optionalEntity.get();
            if (pacientesDTO.getNombre() != null) entity.setPacienteNombre(pacientesDTO.getNombre());
            if (pacientesDTO.getApellido() != null) entity.setPacienteApellido(pacientesDTO.getApellido());
            if (pacientesDTO.getFechaNacimiento() != null) entity.setPacientefechaNacimiento(pacientesDTO.getFechaNacimiento());
            if (pacientesDTO.getEmail() != null) entity.setPacienteEmail(pacientesDTO.getEmail());

            this.pacientesRepository.save(entity);
        } catch (Exception exception) {
            throw new UpdateException("Error actualizando paciente: " + exception.getMessage(), PacientesEntity.class.getName());
        }
    }

    public void delete(Integer id) throws DeleteException {
        try {
            Optional<PacientesEntity> optionalEntity = this.pacientesRepository.findById(id);
            if (optionalEntity.isEmpty()) {
                throw new DeleteException("Paciente no encontrado", PacientesEntity.class.getName());
            }

            PacientesEntity paciente = optionalEntity.get();
            if (paciente.getCitas() != null && !paciente.getCitas().isEmpty()) {
                throw new DeleteException("No se puede eliminar el paciente porque tiene citas programadas", PacientesEntity.class.getName());
            }

            this.pacientesRepository.delete(paciente);
        } catch (DeleteException de) {
            throw de;
        } catch (Exception exception) {
            throw new DeleteException("No se puede eliminar el paciente porque tiene citas programadas", PacientesEntity.class.getName());
        }
    }
}
