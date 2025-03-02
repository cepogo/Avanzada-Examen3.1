package ec.edu.espe.ProyectoClinica.integration;

import ec.edu.espe.ProyectoClinica.api.dto.CitaDTO;
import ec.edu.espe.ProyectoClinica.entity.*;
import ec.edu.espe.ProyectoClinica.repository.*;
import ec.edu.espe.ProyectoClinica.service.CitasService;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional(propagation = Propagation.NEVER)
public class CitasAgendamientoTest {

    @Autowired
    private CitasService citasService;
    @Autowired
    private CitasRepository citasRepository;
    @Autowired
    private PacientesRepository pacientesRepository;
    @Autowired
    private MedicosRepository medicosRepository;
    @Autowired
    private ConsultoriosRepository consultoriosRepository;

    private PacientesEntity paciente;
    private MedicosEntity medico;
    private ConsultoriosEntity consultorio;
    private Integer citaId;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeAll
    public void setUp() {
        // Limpiar datos existentes
        citasRepository.deleteAll();
        pacientesRepository.deleteAll();
        medicosRepository.deleteAll();
        consultoriosRepository.deleteAll();

        // Crear paciente de prueba
        paciente = new PacientesEntity();
        paciente.setPacienteNombre("Fer");
        paciente.setPacienteApellido("Suquillos");
        paciente.setPacienteEmail("fer@gmail.com");
        paciente.setPacientefechaNacimiento(new Date());
        paciente = pacientesRepository.save(paciente);

        // Crear médico de prueba
        medico = new MedicosEntity();
        medico.setMedicoNombre("Dr. Mata");
        medico.setMedicoApellido("Sanos");
        medico.setMedicoEspecialidad("Cardiología");
        medico = medicosRepository.save(medico);

        // Crear consultorio de prueba
        consultorio = new ConsultoriosEntity();
        consultorio.setConsultorioNumero("101");
        consultorio.setConsultorioPiso(1);
        consultorio = consultoriosRepository.save(consultorio);
    }

    @Test
    @Order(1)
    @DisplayName("Test: Creación de una nueva cita médica")
    public void testCreacionCita() {
        try {
            // Crear DTO con los datos de la cita
            CitaDTO citaDTO = new CitaDTO();
            citaDTO.setPacienteId(paciente.getPacienteId());
            citaDTO.setMedicoId(medico.getMedicoId());
            citaDTO.setFecha(new Date()); // Fecha actual
            citaDTO.setHora("10:00:00");
            citaDTO.setConsultorioId(consultorio.getConsultorioId());

            // Crear la cita
            citasService.create(citaDTO);

            // Verificar que la cita se creó correctamente
            List<CitaDTO> citas = citasService.getAllCitas();
            assertFalse(citas.isEmpty(), "La lista de citas no debería estar vacía");

            CitaDTO citaCreada = citas.get(citas.size() - 1);
            citaId = citaCreada.getId();

            // Verificaciones
            assertNotNull(citaId, "El ID de la cita no debería ser null");
            assertEquals(paciente.getPacienteId(), citaCreada.getPacienteId(), "El paciente asignado debe ser correcto");
            assertEquals(medico.getMedicoId(), citaCreada.getMedicoId(), "El médico asignado debe ser correcto");
            assertEquals("10:00:00", citaCreada.getHora(), "La hora debe coincidir");
            assertEquals(consultorio.getConsultorioId(), citaCreada.getConsultorioId(), "El consultorio debe coincidir");
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }


    @Test
    @Order(2)
    @DisplayName("Test: Modificación de una cita médica")
    public void testModificacionCita() {
        assertNotNull(citaId, "Debe existir una cita para modificar");
        
        try {
            // Crear DTO para la actualización
            CitaDTO citaDTO = new CitaDTO();
            citaDTO.setId(citaId);
            citaDTO.setPacienteId(paciente.getPacienteId());
            citaDTO.setMedicoId(medico.getMedicoId());
            citaDTO.setConsultorioId(consultorio.getConsultorioId());
            citaDTO.setFecha(new Date());
            citaDTO.setHora("11:30:00"); // Nueva hora

            // Actualizar la cita
            citasService.update(citaId, citaDTO);

            // Verificar la actualización
            CitasEntity citaActualizada = citasRepository.findById(citaId)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            // Verificaciones
            assertEquals("11:30:00", citaActualizada.getCitaHora().toString(), "La hora debe estar actualizada");
            assertEquals(paciente.getPacienteId(), citaActualizada.getPaciente().getPacienteId(), "El paciente debe mantenerse");
            assertEquals(medico.getMedicoId(), citaActualizada.getMedico().getMedicoId(), "El médico debe mantenerse");
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test: Eliminación de una cita médica")
    public void testEliminacionCita() {
        assertNotNull(citaId, "Debe existir una cita para eliminar");
        assertTrue(citasRepository.existsById(citaId), "La cita debe existir antes de eliminarla");

        try {
            // Eliminar la cita
            citasService.delete(citaId);

            // Verificar que la cita ya no existe
            assertFalse(citasRepository.existsById(citaId), 
                "La cita no debería existir después de eliminarla");

            // Verificar que el paciente y médico siguen existiendo
            assertTrue(pacientesRepository.existsById(paciente.getPacienteId()),
                "El paciente debe seguir existiendo");
            assertTrue(medicosRepository.existsById(medico.getMedicoId()),
                "El médico debe seguir existiendo");
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @AfterAll
    public void tearDown() {
        // Limpiar todos los datos de prueba
        citasRepository.deleteAll();
        pacientesRepository.deleteAll();
        medicosRepository.deleteAll();
        consultoriosRepository.deleteAll();
    }
} 