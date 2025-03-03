package ec.edu.espe.ProyectoClinica.service;

import ec.edu.espe.ProyectoClinica.api.dto.MedicoDTO;
import ec.edu.espe.ProyectoClinica.entity.MedicosEntity;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.repository.MedicosRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicosService {
    private MedicosRepository medicosRepository;

    private String msgError;

    public MedicosService(MedicosRepository medicosRepository) {
        this.medicosRepository = medicosRepository;
    }

    public List<MedicoDTO> getAllMedicos() throws DocumentNotFoundException {
        try {
            List<MedicosEntity> medicosEntities = this.medicosRepository.findAll();
            List<MedicoDTO> medicosDTOs = new ArrayList<>();

            for (MedicosEntity medicosEntity : medicosEntities) {
                MedicoDTO medicosDTO = new MedicoDTO();
                medicosDTO.setId(medicosEntity.getMedicoId());
                medicosDTO.setNombre(medicosEntity.getMedicoNombre());
                medicosDTO.setApellido(medicosEntity.getMedicoApellido());
                medicosDTO.setEspecialidad(medicosEntity.getMedicoEspecialidad());

                medicosDTOs.add(medicosDTO);
            }

            return medicosDTOs;
        } catch (Exception exception) {
            throw new DocumentNotFoundException("No hay médicos registrados", "Médicos");
        }
    }

    public void create(MedicoDTO medicoDTO) throws InsertException {
        try {
            // Validar campos requeridos
            if (medicoDTO.getNombre() == null || medicoDTO.getNombre().trim().isEmpty()) {
                throw new InsertException("Debe ingresar el nombre del médico", "Médicos");
            }
            if (medicoDTO.getApellido() == null || medicoDTO.getApellido().trim().isEmpty()) {
                throw new InsertException("Debe ingresar el apellido del médico", "Médicos");
            }
            if (medicoDTO.getEspecialidad() == null || medicoDTO.getEspecialidad().trim().isEmpty()) {
                throw new InsertException("Debe ingresar la especialidad del médico", "Médicos");
            }

            // Validar duplicados de nombre y apellido
            if (medicosRepository.existsByMedicoNombreAndMedicoApellido(
                    medicoDTO.getNombre(), medicoDTO.getApellido())) {
                throw new InsertException(
                    "No se puede registrar el médico. " + medicoDTO.getNombre() + " " + 
                    medicoDTO.getApellido() + " ya está registrado en el sistema",
                    "Médicos");
            }

            MedicosEntity entity = new MedicosEntity();
            entity.setMedicoNombre(medicoDTO.getNombre());
            entity.setMedicoApellido(medicoDTO.getApellido());
            entity.setMedicoEspecialidad(medicoDTO.getEspecialidad());

            this.medicosRepository.save(entity);
        } catch (InsertException e) {
            throw e;
        } catch (Exception exception) {
            throw new InsertException("No se pudo registrar el médico", "Médicos");
        }
    }

    public void update(Integer id, MedicoDTO medicoDTO) throws UpdateException {
        try {
            if (id == null) {
                throw new UpdateException("Debe especificar el ID del médico a actualizar", "Médicos");
            }

            Optional<MedicosEntity> optionalEntity = this.medicosRepository.findById(id);
            if (optionalEntity.isEmpty()) {
                throw new UpdateException("No existe un médico con el ID especificado", "Médicos");
            }

            MedicosEntity existingMedico = optionalEntity.get();

            // Validar duplicados de nombre y apellido solo si son diferentes a los actuales
            if ((medicoDTO.getNombre() != null && !existingMedico.getMedicoNombre().equals(medicoDTO.getNombre())) ||
                (medicoDTO.getApellido() != null && !existingMedico.getMedicoApellido().equals(medicoDTO.getApellido()))) {
                if (medicosRepository.existsByMedicoNombreAndMedicoApellido(
                        medicoDTO.getNombre() != null ? medicoDTO.getNombre() : existingMedico.getMedicoNombre(),
                        medicoDTO.getApellido() != null ? medicoDTO.getApellido() : existingMedico.getMedicoApellido())) {
                    throw new UpdateException(
                        "No se puede actualizar el médico. " + 
                        (medicoDTO.getNombre() != null ? medicoDTO.getNombre() : existingMedico.getMedicoNombre()) + " " +
                        (medicoDTO.getApellido() != null ? medicoDTO.getApellido() : existingMedico.getMedicoApellido()) + 
                        " ya está registrado en el sistema",
                        "Médicos");
                }
            }

            if (medicoDTO.getNombre() != null) existingMedico.setMedicoNombre(medicoDTO.getNombre());
            if (medicoDTO.getApellido() != null) existingMedico.setMedicoApellido(medicoDTO.getApellido());
            if (medicoDTO.getEspecialidad() != null) existingMedico.setMedicoEspecialidad(medicoDTO.getEspecialidad());

            this.medicosRepository.save(existingMedico);
        } catch (UpdateException e) {
            throw e;
        } catch (Exception exception) {
            throw new UpdateException("No se pudo actualizar el médico", "Médicos");
        }
    }

    public void delete(Integer id) throws DeleteException {
        try {
            Optional<MedicosEntity> medicosEntityOptional = this.medicosRepository.findById(id);
            if (!medicosEntityOptional.isPresent()) {
                throw new DeleteException("No existe un médico con el ID especificado", "Médicos");
            }

            MedicosEntity medico = medicosEntityOptional.get();
            if (medico.getCitas() != null && !medico.getCitas().isEmpty()) {
                throw new DeleteException(
                    "No se puede eliminar el médico porque tiene citas pendientes. Primero debe cancelar las citas",
                    "Médicos");
            }

            this.medicosRepository.delete(medico);
        } catch (DeleteException de) {
            throw de;
        } catch (Exception exception) {
            throw new DeleteException("No se pudo eliminar el médico", "Médicos");
        }
    }
}
