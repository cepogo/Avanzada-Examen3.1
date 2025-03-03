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
            throw new DocumentNotFoundException("No hay consultorios registrados", "Consultorios");
        }
    }

    public void create(ConsultoriosDTO consultoriosDTO) throws InsertException {
        try {
            // Validar campos requeridos
            if (consultoriosDTO.getNumero() == null || consultoriosDTO.getNumero().trim().isEmpty()) {
                throw new InsertException("Debe ingresar el número de consultorio", "Consultorios");
            }
            if (consultoriosDTO.getPiso() == null) {
                throw new InsertException("Debe ingresar el piso del consultorio", "Consultorios");
            }

            // Validar que no exista un consultorio con el mismo número en el mismo piso
            if (consultoriosRepository.existsByConsultorioNumeroAndConsultorioPiso(
                    consultoriosDTO.getNumero(), consultoriosDTO.getPiso())) {
                throw new InsertException(
                    "No se puede registrar el consultorio. El consultorio " + consultoriosDTO.getNumero() + 
                    " ya existe en el piso " + consultoriosDTO.getPiso(),
                    "Consultorios");
            }

            ConsultoriosEntity consultorioToCreate = new ConsultoriosEntity();
            consultorioToCreate.setConsultorioNumero(consultoriosDTO.getNumero());
            consultorioToCreate.setConsultorioPiso(consultoriosDTO.getPiso());

            this.consultoriosRepository.save(consultorioToCreate);
        } catch (InsertException e) {
            throw e;
        } catch (Exception exception) {
            throw new InsertException("No se pudo registrar el consultorio", "Consultorios");
        }
    }

    public void update(ConsultoriosDTO consultoriosDTO) throws UpdateException {
        try {
            if (consultoriosDTO.getId() == null) {
                throw new UpdateException("Debe especificar el ID del consultorio a actualizar", "Consultorios");
            }

            Optional<ConsultoriosEntity> optionalEntity = this.consultoriosRepository.findById(consultoriosDTO.getId());
            if (optionalEntity.isEmpty()) {
                throw new UpdateException("No existe un consultorio con el ID especificado", "Consultorios");
            }

            ConsultoriosEntity existingConsultorio = optionalEntity.get();

            // Validar duplicados de número y piso solo si son diferentes a los actuales
            if ((consultoriosDTO.getNumero() != null && !existingConsultorio.getConsultorioNumero().equals(consultoriosDTO.getNumero())) ||
                (consultoriosDTO.getPiso() != null && !existingConsultorio.getConsultorioPiso().equals(consultoriosDTO.getPiso()))) {
                if (consultoriosRepository.existsByConsultorioNumeroAndConsultorioPiso(
                        consultoriosDTO.getNumero() != null ? consultoriosDTO.getNumero() : existingConsultorio.getConsultorioNumero(),
                        consultoriosDTO.getPiso() != null ? consultoriosDTO.getPiso() : existingConsultorio.getConsultorioPiso())) {
                    throw new UpdateException(
                        "No se puede actualizar el consultorio. El consultorio " + 
                        (consultoriosDTO.getNumero() != null ? consultoriosDTO.getNumero() : existingConsultorio.getConsultorioNumero()) +
                        " ya existe en el piso " + 
                        (consultoriosDTO.getPiso() != null ? consultoriosDTO.getPiso() : existingConsultorio.getConsultorioPiso()),
                        "Consultorios");
                }
            }

            if (consultoriosDTO.getNumero() != null) existingConsultorio.setConsultorioNumero(consultoriosDTO.getNumero());
            if (consultoriosDTO.getPiso() != null) existingConsultorio.setConsultorioPiso(consultoriosDTO.getPiso());

            this.consultoriosRepository.save(existingConsultorio);
        } catch (UpdateException e) {
            throw e;
        } catch (Exception exception) {
            throw new UpdateException("No se pudo actualizar el consultorio", "Consultorios");
        }
    }

    public void delete(Integer id) throws DeleteException {
        try {
            Optional<ConsultoriosEntity> optionalEntity = this.consultoriosRepository.findById(id);
            if (optionalEntity.isEmpty()) {
                throw new DeleteException("No existe un consultorio con el ID especificado", "Consultorios");
            }

            ConsultoriosEntity consultorio = optionalEntity.get();
            if (consultorio.getCitas() != null && !consultorio.getCitas().isEmpty()) {
                throw new DeleteException(
                    "No se puede eliminar el consultorio porque tiene citas pendientes. Primero debe cancelar las citas",
                    "Consultorios");
            }

            this.consultoriosRepository.delete(consultorio);
        } catch (DeleteException de) {
            throw de;
        } catch (Exception exception) {
            throw new DeleteException("No se pudo eliminar el consultorio", "Consultorios");
        }
    }
}



