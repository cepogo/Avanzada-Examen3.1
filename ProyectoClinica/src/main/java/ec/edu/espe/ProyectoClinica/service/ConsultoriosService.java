package ec.edu.espe.ProyectoClinica.service;

import ec.edu.espe.ProyectoClinica.api.dto.ConsultoriosDTO;
import ec.edu.espe.ProyectoClinica.entity.ConsultoriosEntity;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.repository.ConsultoriosRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultoriosService {

    private final ConsultoriosRepository consultoriosRepository;

    private String msgError;

    public ConsultoriosService(ConsultoriosRepository consultoriosRepository) {
        this.consultoriosRepository = consultoriosRepository;
    }

    public List<ConsultoriosDTO> getAllConsultorios() throws DocumentNotFoundException {
        try {
            List<ConsultoriosEntity> consultoriosEntities = this.consultoriosRepository.findAll();
            List<ConsultoriosDTO> consultoriosDTOs = new ArrayList<>();

            for (ConsultoriosEntity consultoriosEntity : consultoriosEntities) {
                ConsultoriosDTO consultoriosDTO = new ConsultoriosDTO();
                consultoriosDTO.setId(consultoriosEntity.getConsultorioId());
                consultoriosDTO.setNumero(consultoriosEntity.getConsultorioNumero());
                consultoriosDTO.setPiso(consultoriosEntity.getConsultorioPiso());

                consultoriosDTOs.add(consultoriosDTO);
            }

            return consultoriosDTOs;
        } catch (Exception exception) {
            throw new DocumentNotFoundException("No se encontraron consultorios", ConsultoriosEntity.class.getName());
        }
    }
    public void create(ConsultoriosDTO consultoriosDTO) throws InsertException {
        try {
            ConsultoriosEntity consultorioToCreate = new ConsultoriosEntity();
            consultorioToCreate.setConsultorioNumero(consultoriosDTO.getNumero());
            consultorioToCreate.setConsultorioPiso(consultoriosDTO.getPiso());

            this.consultoriosRepository.save(consultorioToCreate);
        }catch (Exception exception){
            this.msgError = this.msgError == null ? "Error al crear consultorio" : this.msgError;
            throw new InsertException(this.msgError, ConsultoriosEntity.class.getName());
        }
    }

    public void update(ConsultoriosDTO consultoriosDTO) throws UpdateException {
        try {
            ConsultoriosEntity consultorioToUpdate = this.consultoriosRepository.findByConsultorioNumero(consultoriosDTO.getNumero());
            if (consultorioToUpdate == null) {
                throw new UpdateException("No se encontró el consultorio", ConsultoriosEntity.class.getName());
            }

            // Actualizar solo los campos que no son nulos
            if (consultoriosDTO.getNumero() != null) {
                consultorioToUpdate.setConsultorioNumero(consultoriosDTO.getNumero());
            }
            if (consultoriosDTO.getPiso() != null) {
                consultorioToUpdate.setConsultorioPiso(consultoriosDTO.getPiso());
            }

            this.consultoriosRepository.save(consultorioToUpdate);
        } catch (Exception exception) {
            throw new UpdateException("Error al actualizar consultorio: " + exception.getMessage(), ConsultoriosEntity.class.getName());
        }
    }

    public void delete(Integer id) throws DeleteException {
        try {
            Optional<ConsultoriosEntity> optionalEntity = this.consultoriosRepository.findById(id);
            if (optionalEntity.isEmpty()) {
                throw new DeleteException("Consultorio no encontrado", ConsultoriosEntity.class.getName());
            }

            ConsultoriosEntity consultorio = optionalEntity.get();
            if (consultorio.getCitas() != null && !consultorio.getCitas().isEmpty()) {
                throw new DeleteException("No se puede eliminar el consultorio porque tiene citas programadas", ConsultoriosEntity.class.getName());
            }

            this.consultoriosRepository.delete(consultorio);
        } catch (DeleteException de) {
            throw de;
        } catch (Exception exception) {
            throw new DeleteException("Error eliminando consultorio: " + exception.getMessage(), ConsultoriosEntity.class.getName());
        }
    }
}



