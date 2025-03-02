package ec.edu.espe.ProyectoClinica.service;

import ec.edu.espe.ProyectoClinica.api.dto.CitaDTO;
import ec.edu.espe.ProyectoClinica.entity.CitasEntity;
import ec.edu.espe.ProyectoClinica.entity.ConsultoriosEntity;
import ec.edu.espe.ProyectoClinica.entity.MedicosEntity;
import ec.edu.espe.ProyectoClinica.entity.PacientesEntity;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.repository.CitasRepository;
import ec.edu.espe.ProyectoClinica.repository.ConsultoriosRepository;
import ec.edu.espe.ProyectoClinica.repository.MedicosRepository;
import ec.edu.espe.ProyectoClinica.repository.PacientesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CitasService {

    private final CitasRepository citasRepository;
    private final PacientesRepository pacientesRepository;
    private final ConsultoriosRepository consultoriosRepository;
    private final MedicosRepository medicosRepository;

    public CitasService(CitasRepository citasRepository, PacientesRepository pacientesRepository, 
                       ConsultoriosRepository consultoriosRepository, MedicosRepository medicosRepository) {
        this.citasRepository = citasRepository;
        this.pacientesRepository = pacientesRepository;
        this.consultoriosRepository = consultoriosRepository;
        this.medicosRepository = medicosRepository;
    }

    public List<CitaDTO> getAllCitas() throws DocumentNotFoundException {
        try {
            System.out.println("Iniciando búsqueda de todas las citas");
            List<CitasEntity> citasEntities = this.citasRepository.findAll();
            System.out.println("Citas encontradas en la base de datos: " + citasEntities.size());
            
            List<CitaDTO> citasDTOs = new ArrayList<>();
            for (CitasEntity citaEntity : citasEntities) {
                try {
                    CitaDTO citaDTO = new CitaDTO();
                    citaDTO.setId(citaEntity.getCitasId());
                    citaDTO.setPacienteId(citaEntity.getPaciente().getPacienteId());
                    citaDTO.setMedicoId(citaEntity.getMedico().getMedicoId());
                    citaDTO.setFecha(citaEntity.getCitaFecha());
                    citaDTO.setHora(citaEntity.getCitaHora() != null ? citaEntity.getCitaHora().toString() : null);
                    citaDTO.setConsultorioId(citaEntity.getConsultorio().getConsultorioId());
                    citasDTOs.add(citaDTO);
                } catch (Exception e) {
                    System.err.println("Error al mapear cita: " + citaEntity + ". Error: " + e.getMessage());
                }
            }
            System.out.println("DTOs de citas creados exitosamente: " + citasDTOs.size());
            return citasDTOs;
        } catch (Exception exception) {
            System.err.println("Error al obtener citas: " + exception.getMessage());
            exception.printStackTrace();
            throw new DocumentNotFoundException("No se encontraron citas: " + exception.getMessage(), CitasEntity.class.getName());
        }
    }

    public void create(CitaDTO citaDTO) throws InsertException {
        try {
            System.out.println("Iniciando creación de cita con datos: " + citaDTO);
            
            // Validación más estricta de campos
            if (citaDTO == null) {
                throw new InsertException("Los datos de la cita son requeridos", CitasEntity.class.getName());
            }
            
            System.out.println("Validando campos de la cita...");
            System.out.println("pacienteId: " + citaDTO.getPacienteId());
            System.out.println("medicoId: " + citaDTO.getMedicoId());
            System.out.println("consultorioId: " + citaDTO.getConsultorioId());
            System.out.println("fecha: " + citaDTO.getFecha());
            System.out.println("hora: " + citaDTO.getHora());

            if (citaDTO.getPacienteId() == null || citaDTO.getPacienteId() <= 0) {
                throw new InsertException("El ID del paciente es inválido: " + citaDTO.getPacienteId(), CitasEntity.class.getName());
            }
            if (citaDTO.getMedicoId() == null || citaDTO.getMedicoId() <= 0) {
                throw new InsertException("El ID del médico es inválido: " + citaDTO.getMedicoId(), CitasEntity.class.getName());
            }
            if (citaDTO.getConsultorioId() == null || citaDTO.getConsultorioId() <= 0) {
                throw new InsertException("El ID del consultorio es inválido: " + citaDTO.getConsultorioId(), CitasEntity.class.getName());
            }
            if (citaDTO.getFecha() == null) {
                throw new InsertException("La fecha es requerida", CitasEntity.class.getName());
            }
            if (citaDTO.getHora() == null || citaDTO.getHora().trim().isEmpty()) {
                throw new InsertException("La hora es requerida", CitasEntity.class.getName());
            }

            // Buscar entidades relacionadas
            System.out.println("Buscando paciente con ID: " + citaDTO.getPacienteId());
            PacientesEntity paciente = pacientesRepository.findById(citaDTO.getPacienteId())
                    .orElseThrow(() -> new InsertException("Paciente no encontrado con ID: " + citaDTO.getPacienteId(), CitasEntity.class.getName()));
            System.out.println("Paciente encontrado: " + paciente);

            System.out.println("Buscando médico con ID: " + citaDTO.getMedicoId());
            MedicosEntity medico = medicosRepository.findById(citaDTO.getMedicoId())
                    .orElseThrow(() -> new InsertException("Médico no encontrado con ID: " + citaDTO.getMedicoId(), CitasEntity.class.getName()));
            System.out.println("Médico encontrado: " + medico);

            System.out.println("Buscando consultorio con ID: " + citaDTO.getConsultorioId());
            ConsultoriosEntity consultorio = consultoriosRepository.findById(citaDTO.getConsultorioId())
                    .orElseThrow(() -> new InsertException("Consultorio no encontrado con ID: " + citaDTO.getConsultorioId(), CitasEntity.class.getName()));
            System.out.println("Consultorio encontrado: " + consultorio);

            // Crear y configurar la entidad
            CitasEntity citaToCreate = new CitasEntity();
            citaToCreate.setPaciente(paciente);
            citaToCreate.setMedico(medico);
            citaToCreate.setConsultorio(consultorio);
            
            // Mantener la fecha tal como viene, sin ajustes de zona horaria
            citaToCreate.setCitaFecha(citaDTO.getFecha());
            
            try {
                String horaFormateada = citaDTO.getHora().trim();
                if (!horaFormateada.matches("\\d{2}:\\d{2}(:\\d{2})?")) {
                    horaFormateada = horaFormateada + ":00";
                }
                System.out.println("Formateando hora: " + horaFormateada);
                // Asegurarse de que la hora tenga el formato correcto para SQL Time
                if (horaFormateada.length() == 5) {
                    horaFormateada += ":00";
                }
                citaToCreate.setCitaHora(java.sql.Time.valueOf(horaFormateada));
            } catch (IllegalArgumentException e) {
                System.err.println("Error al formatear la hora: " + e.getMessage());
                throw new InsertException("Formato de hora inválido: " + citaDTO.getHora() + ". El formato debe ser HH:mm o HH:mm:ss", CitasEntity.class.getName());
            }

            // Guardar la cita
            System.out.println("Intentando guardar la cita: " + citaToCreate);
            CitasEntity savedCita = citasRepository.save(citaToCreate);
            System.out.println("Cita creada exitosamente: " + savedCita);
        } catch (InsertException e) {
            System.err.println("Error de inserción específico: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error general al crear cita: " + e.getMessage());
            e.printStackTrace();
            throw new InsertException("Error al crear la cita: " + e.getMessage(), CitasEntity.class.getName());
        }
    }

    public void update(Integer id, CitaDTO citaDTO) throws UpdateException {
        try {
            System.out.println("Actualizando cita con ID: " + id + ", datos: " + citaDTO);
            
            if (id == null) {
                throw new UpdateException("El ID de la cita es requerido", CitasEntity.class.getName());
            }

            CitasEntity citaExistente = citasRepository.findById(id)
                    .orElseThrow(() -> new UpdateException("Cita no encontrada", CitasEntity.class.getName()));

            validateFields(citaDTO);

            PacientesEntity paciente = findPaciente(citaDTO.getPacienteId());
            MedicosEntity medico = findMedico(citaDTO.getMedicoId());
            ConsultoriosEntity consultorio = findConsultorio(citaDTO.getConsultorioId());

            mapDTOToEntity(citaDTO, citaExistente, paciente, medico, consultorio);

            CitasEntity updatedCita = citasRepository.save(citaExistente);
            System.out.println("Cita actualizada exitosamente: " + updatedCita);
        } catch (UpdateException | InsertException e) {
            System.err.println("Error de actualización: " + e.getMessage());
            throw new UpdateException(e.getMessage(), CitasEntity.class.getName());
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();
            throw new UpdateException("Error al actualizar la cita: " + e.getMessage(), CitasEntity.class.getName());
        }
    }

    private void validateFields(CitaDTO citaDTO) throws InsertException {
        if (citaDTO == null) {
            throw new InsertException("Los datos de la cita son requeridos", CitasEntity.class.getName());
        }
        if (citaDTO.getPacienteId() == null) {
            throw new InsertException("El ID del paciente es requerido", CitasEntity.class.getName());
        }
        if (citaDTO.getMedicoId() == null) {
            throw new InsertException("El ID del médico es requerido", CitasEntity.class.getName());
        }
        if (citaDTO.getFecha() == null) {
            throw new InsertException("La fecha es requerida", CitasEntity.class.getName());
        }
        if (citaDTO.getHora() == null || citaDTO.getHora().trim().isEmpty()) {
            throw new InsertException("La hora es requerida", CitasEntity.class.getName());
        }
        if (citaDTO.getConsultorioId() == null) {
            throw new InsertException("El ID del consultorio es requerido", CitasEntity.class.getName());
        }
    }

    private PacientesEntity findPaciente(Integer pacienteId) throws InsertException {
        return pacientesRepository.findById(pacienteId)
                .orElseThrow(() -> new InsertException("Paciente no encontrado", CitasEntity.class.getName()));
    }

    private MedicosEntity findMedico(Integer medicoId) throws InsertException {
        return medicosRepository.findById(medicoId)
                .orElseThrow(() -> new InsertException("Médico no encontrado", CitasEntity.class.getName()));
    }

    private ConsultoriosEntity findConsultorio(Integer consultorioId) throws InsertException {
        return consultoriosRepository.findById(consultorioId)
                .orElseThrow(() -> new InsertException("Consultorio no encontrado", CitasEntity.class.getName()));
    }

    private void mapDTOToEntity(CitaDTO dto, CitasEntity entity, 
                              PacientesEntity paciente, 
                              MedicosEntity medico, 
                              ConsultoriosEntity consultorio) throws InsertException {
        entity.setPaciente(paciente);
        entity.setMedico(medico);
        entity.setCitaFecha(dto.getFecha());
        
        String horaFormateada = dto.getHora().trim();
        if (!horaFormateada.matches("\\d{2}:\\d{2}(:\\d{2})?")) {
            horaFormateada = horaFormateada + ":00";
        }
        try {
            entity.setCitaHora(java.sql.Time.valueOf(horaFormateada));
        } catch (IllegalArgumentException e) {
            throw new InsertException("Formato de hora inválido: " + horaFormateada, CitasEntity.class.getName());
        }
        
        entity.setConsultorio(consultorio);
    }

    public void delete(Integer id) throws DeleteException {
        try {
            CitasEntity citaToDelete = citasRepository.findById(id)
                    .orElseThrow(() -> new DeleteException("Cita no encontrada", CitasEntity.class.getName()));

            citasRepository.delete(citaToDelete);
        } catch (DeleteException e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar la cita: " + e.getMessage(), CitasEntity.class.getName());
        }
    }
}
