package ec.edu.espe.ProyectoClinica.integration;

import ec.edu.espe.ProyectoClinica.api.dto.CitaDTO;
import ec.edu.espe.ProyectoClinica.entity.*;
import ec.edu.espe.ProyectoClinica.repository.*;
import ec.edu.espe.ProyectoClinica.service.*;
import ec.edu.espe.ProyectoClinica.exception.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CitasIntegrationTest {

    @Autowired
    private CitasService citasService;
    @Autowired
    private PacientesService pacientesService;
    @Autowired
    private MedicosService medicosService;
    @Autowired
    private CitasRepository citasRepository;
    @Autowired
    private PacientesRepository pacientesRepository;
    @Autowired
    private MedicosRepository medicosRepository;
    @Autowired
    private ConsultoriosRepository consultoriosRepository;

    private static PacientesEntity paciente;
    private static MedicosEntity medico;
    private static ConsultoriosEntity consultorio;
    private static Integer citaId;

    @BeforeAll
    static void setUp(@Autowired PacientesRepository pacientesRepo,
                     @Autowired MedicosRepository medicosRepo,
                     @Autowired ConsultoriosRepository consultoriosRepo) {
        // Crear paciente de prueba
        paciente = new PacientesEntity();
        paciente.setPacienteNombre("Diegosh");
        paciente.setPacienteApellido("Cadenash");
        paciente.setPacienteEmail("cadenash@gmail.com");
        paciente.setPacientefechaNacimiento(new Date());
        paciente = pacientesRepo.save(paciente);

        // Crear médico de prueba
        medico = new MedicosEntity();
        medico.setMedicoNombre("Dr. Mata");
        medico.setMedicoApellido("Sanos");
        medico.setMedicoEspecialidad("Cardiología");
        medico = medicosRepo.save(medico);

        // Crear consultorio de prueba
        consultorio = new ConsultoriosEntity();
        consultorio.setConsultorioNumero("512");
        consultorio.setConsultorioPiso(1);
        consultorio = consultoriosRepo.save(consultorio);
    }

    @Test
    @Order(1)
    void deberiaCrearCitaCorrectamente() throws InsertException, DocumentNotFoundException {
        // Given
        CitaDTO nuevaCita = new CitaDTO();
        nuevaCita.setPacienteId(paciente.getPacienteId());
        nuevaCita.setMedicoId(medico.getMedicoId());
        nuevaCita.setConsultorioId(consultorio.getConsultorioId());
        nuevaCita.setFecha(new Date());
        nuevaCita.setHora("14:30:00");

        // When
        citasService.create(nuevaCita);
        List<CitaDTO> citas = citasService.getAllCitas();
        citaId = citas.get(0).getId();

        // Then
        assertFalse(citas.isEmpty(), "Debería existir al menos una cita");
        assertEquals(paciente.getPacienteId(), citas.get(0).getPacienteId(), "El ID del paciente debe coincidir");
        assertEquals(medico.getMedicoId(), citas.get(0).getMedicoId(), "El ID del médico debe coincidir");
    }

    @Test
    @Order(2)
    void noDeberiaPermitirEliminarPacienteConCita() {
        // When & Then
        DeleteException exception = assertThrows(
            DeleteException.class,
            () -> pacientesService.delete(paciente.getPacienteId())
        );
        
        assertTrue(pacientesRepository.existsById(paciente.getPacienteId()), 
            "El paciente debería seguir existiendo");
        assertEquals("Error en el sistema. No se pudo completar la eliminación del paciente", 
            exception.getMessage());
    }

    @Test
    @Order(3)
    void noDeberiaPermitirEliminarMedicoConCita() {
        // When & Then
        DeleteException exception = assertThrows(
            DeleteException.class,
            () -> medicosService.delete(medico.getMedicoId())
        );

        assertTrue(medicosRepository.existsById(medico.getMedicoId()), 
            "El médico debería seguir existiendo");
        assertEquals("No se pudo eliminar el médico", 
            exception.getMessage());
    }

    @Test
    @Order(4)
    void deberiaActualizarCitaCorrectamente() throws UpdateException {
        // Given
        CitaDTO citaActualizada = new CitaDTO();
        citaActualizada.setId(citaId);
        citaActualizada.setPacienteId(paciente.getPacienteId());
        citaActualizada.setMedicoId(medico.getMedicoId());
        citaActualizada.setConsultorioId(consultorio.getConsultorioId());
        citaActualizada.setFecha(new Date());
        citaActualizada.setHora("15:00:00");

        // When
        citasService.update(citaId, citaActualizada);
        CitasEntity citaModificada = citasRepository.findById(citaId)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Then
        assertEquals("15:00:00", citaModificada.getCitaHora().toString(), 
            "La hora de la cita debería haberse actualizado");
    }

    @AfterAll
    static void limpiar(@Autowired CitasRepository citasRepo,
                       @Autowired PacientesRepository pacientesRepo,
                       @Autowired MedicosRepository medicosRepo,
                       @Autowired ConsultoriosRepository consultoriosRepo) {
        citasRepo.deleteAll();
        pacientesRepo.deleteAll();
        medicosRepo.deleteAll();
        consultoriosRepo.deleteAll();
    }
} 