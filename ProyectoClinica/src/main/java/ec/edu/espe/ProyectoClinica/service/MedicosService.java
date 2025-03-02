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
        }catch (Exception exception){
            this.msgError = this.msgError == null ? "No se encontro medicos": this.msgError;
            throw new DocumentNotFoundException(this.msgError, MedicosEntity.class.getName());
        }
    }

    public void create (MedicoDTO medicoDTO) throws InsertException {
        try {
            MedicosEntity medicoToCreate = new MedicosEntity();
            medicoToCreate.setMedicoNombre(medicoDTO.getNombre());
            medicoToCreate.setMedicoApellido(medicoDTO.getApellido());
            medicoToCreate.setMedicoEspecialidad(medicoDTO.getEspecialidad());

            this.medicosRepository.save(medicoToCreate);
        }catch (Exception exception){
            this.msgError = this.msgError == null ? "Error al crear medico": this.msgError;
            throw new InsertException(this.msgError, MedicosEntity.class.getName());
        }
    }

    public void update (Integer id, MedicoDTO medicoDTO) throws UpdateException {
        try {
            if (id == null) {
                throw new UpdateException("El ID del médico es requerido", MedicosEntity.class.getName());
            }

            Optional<MedicosEntity> medicosEntityOptional = this.medicosRepository.findById(id);
            if (!medicosEntityOptional.isPresent()) {
                throw new UpdateException("Médico no encontrado", MedicosEntity.class.getName());
            }

            MedicosEntity medicoToUpdate = medicosEntityOptional.get();
            
            // Validar que al menos un campo sea proporcionado
            if (medicoDTO.getNombre() == null && medicoDTO.getApellido() == null && medicoDTO.getEspecialidad() == null) {
                throw new UpdateException("Debe proporcionar al menos un campo para actualizar", MedicosEntity.class.getName());
            }

            // Actualizar solo los campos que no son nulos
            if (medicoDTO.getNombre() != null) {
                medicoToUpdate.setMedicoNombre(medicoDTO.getNombre());
            }
            if (medicoDTO.getApellido() != null) {
                medicoToUpdate.setMedicoApellido(medicoDTO.getApellido());
            }
            if (medicoDTO.getEspecialidad() != null) {
                medicoToUpdate.setMedicoEspecialidad(medicoDTO.getEspecialidad());
            }

            this.medicosRepository.save(medicoToUpdate);
        } catch (UpdateException ue) {
            throw ue;
        } catch (Exception exception) {
            throw new UpdateException("Error al actualizar médico: " + exception.getMessage(), MedicosEntity.class.getName());
        }
    }

    public void delete (Integer id) throws DeleteException {
        try {
            Optional<MedicosEntity> medicosEntityOptional = this.medicosRepository.findById(id);
            if (!medicosEntityOptional.isPresent()) {
                throw new DeleteException("Médico no encontrado", MedicosEntity.class.getName());
            }

            MedicosEntity medico = medicosEntityOptional.get();
            if (medico.getCitas() != null && !medico.getCitas().isEmpty()) {
                throw new DeleteException("No se puede eliminar el médico porque tiene citas asociadas", MedicosEntity.class.getName());
            }

            this.medicosRepository.delete(medico);
        } catch (DeleteException de) {
            throw de;
        } catch (Exception exception) {
            throw new DeleteException("No se puede eliminar el médico porque tiene citas asociadas", MedicosEntity.class.getName());
        }
    }
}
