package ec.edu.espe.ProyectoClinica.integration;

import ec.edu.espe.ProyectoClinica.api.dto.CitaDTO;
import ec.edu.espe.ProyectoClinica.entity.*;
import ec.edu.espe.ProyectoClinica.repository.*;
import ec.edu.espe.ProyectoClinica.service.*;
import ec.edu.espe.ProyectoClinica.exception.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CitasIntegrationTest {

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
    @Autowired
    private PacientesService pacientesService;
    @Autowired
    private MedicosService medicosService;

    private PacientesEntity paciente;
    private MedicosEntity medico;
    private ConsultoriosEntity consultorio;
    private Integer citaId;

    @BeforeAll
    public void setUp() {
        // Crear datos de prueba básicos
        paciente = new PacientesEntity();
        paciente.setPacienteNombre("Test");
        paciente.setPacienteApellido("Paciente");
        paciente.setPacienteEmail("test@test.com");
        paciente.setPacientefechaNacimiento(new Date());
        paciente = pacientesRepository.save(paciente);

        medico = new MedicosEntity();
        medico.setMedicoNombre("Test");
        medico.setMedicoApellido("Medico");
        medico.setMedicoEspecialidad("General");
        medico = medicosRepository.save(medico);

        consultorio = new ConsultoriosEntity();
        consultorio.setConsultorioNumero("101");
        consultorio.setConsultorioPiso(1);
        consultorio = consultoriosRepository.save(consultorio);
    }

    @Test
    @Order(1)
    @DisplayName("Test: Crear cita y verificar relaciones")
    public void testCrearCitaConRelaciones() throws InsertException, DocumentNotFoundException {
        CitaDTO citaDTO = new CitaDTO();
        citaDTO.setPacienteId(paciente.getPacienteId());
        citaDTO.setMedicoId(medico.getMedicoId());
        citaDTO.setFecha(new Date());
        citaDTO.setHora("10:00:00");
        citaDTO.setConsultorioId(consultorio.getConsultorioId());

        citasService.create(citaDTO);
        List<CitaDTO> citas = citasService.getAllCitas();
        assertFalse(citas.isEmpty(), "La lista de citas no debería estar vacía");
        
        CitaDTO citaCreada = citas.get(0);
        citaId = citaCreada.getId();

        assertNotNull(citaId, "La cita debe tener un ID válido");
        assertEquals(paciente.getPacienteId(), citaCreada.getPacienteId());
        assertEquals(medico.getMedicoId(), citaCreada.getMedicoId());
    }

    @Test
    @Order(2)
    @DisplayName("Test: No se puede eliminar paciente con citas")
    public void testEliminarPacienteConCitas() {
        DeleteException thrown = assertThrows(
            DeleteException.class,
            () -> pacientesService.delete(paciente.getPacienteId())
        );

        assertEquals("Error en el sistema. No se pudo completar la eliminación del paciente", 
            thrown.getMessage());
        assertTrue(pacientesRepository.existsById(paciente.getPacienteId()));
    }

    @Test
    @Order(3)
    @DisplayName("Test: No se puede eliminar médico con citas")
    public void testEliminarMedicoConCitas() {
        DeleteException thrown = assertThrows(
            DeleteException.class,
            () -> medicosService.delete(medico.getMedicoId())
        );

        assertEquals("No se pudo eliminar el médico", 
            thrown.getMessage());
        assertTrue(medicosRepository.existsById(medico.getMedicoId()));
    }

    @Test
    @Order(4)
    @DisplayName("Test: Actualizar cita")
    public void testActualizarCita() throws UpdateException {
        CitaDTO citaDTO = new CitaDTO();
        citaDTO.setId(citaId);
        citaDTO.setPacienteId(paciente.getPacienteId());
        citaDTO.setMedicoId(medico.getMedicoId());
        citaDTO.setConsultorioId(consultorio.getConsultorioId());
        citaDTO.setFecha(new Date());
        citaDTO.setHora("11:00:00");

        citasService.update(citaId, citaDTO);
        CitasEntity citaActualizada = citasRepository.findById(citaId)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        assertEquals("11:00:00", citaActualizada.getCitaHora().toString());
    }

    @AfterAll
    public void tearDown() {
        citasRepository.deleteAll();
        pacientesRepository.deleteAll();
        medicosRepository.deleteAll();
        consultoriosRepository.deleteAll();
    }
} 