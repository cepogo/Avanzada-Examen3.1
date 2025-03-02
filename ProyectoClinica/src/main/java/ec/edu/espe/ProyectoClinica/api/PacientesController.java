package ec.edu.espe.ProyectoClinica.api;

import ec.edu.espe.ProyectoClinica.api.dto.PacientesDTO;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.service.PacientesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/pacientes")
public class PacientesController {

    private final PacientesService pacientesService;

    public PacientesController(PacientesService pacientesService) {
        this.pacientesService = pacientesService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<PacientesDTO>> getAllPacientes() {
        try {
            List<PacientesDTO> pacientesDTOS =this.pacientesService.getAllPacientes();
            return ResponseEntity.ok().body(pacientesDTOS);
        }catch (DocumentNotFoundException documentNotFoundException){
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody PacientesDTO pacientesDTO) {
        try {
            this.pacientesService.create(pacientesDTO);
            return ResponseEntity.ok().body("Paciente creado correctamente");
        }catch (InsertException insertException){
            return ResponseEntity.badRequest().body(insertException.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity update(@RequestBody PacientesDTO pacientesDTO) {
        try {
            this.pacientesService.update(pacientesDTO);
            return ResponseEntity.ok().body("Paciente creado correctamente");
        }catch (UpdateException updateException){
            return ResponseEntity.badRequest().body(updateException.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("id") Integer id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID del paciente es requerido");
            }
            this.pacientesService.delete(id);
            return ResponseEntity.ok().body("Paciente eliminado correctamente");
        } catch (DeleteException deleteException) {
            return ResponseEntity.badRequest().body(deleteException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el paciente: " + e.getMessage());
        }
    }
}
