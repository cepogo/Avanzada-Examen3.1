package ec.edu.espe.ProyectoClinica.api;

import ec.edu.espe.ProyectoClinica.api.dto.MedicoDTO;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.service.MedicosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/medicos")
public class MedicosController {

    private final MedicosService medicosService;

    public MedicosController(MedicosService medicosService) {
        this.medicosService = medicosService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<MedicoDTO>> getAllMedicos() {
        try {
            List<MedicoDTO> medicoDTOS = this.medicosService.getAllMedicos();
            return ResponseEntity.ok().body(medicoDTOS);
        }catch (DocumentNotFoundException documentNotFoundException){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody MedicoDTO medicoDTO) {
        try {
            this.medicosService.create(medicoDTO);
            return ResponseEntity.ok().body("Medico creado correctamente");
        }catch (InsertException insertException){
            return ResponseEntity.badRequest().body(insertException.getMessage());
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody MedicoDTO medicoDTO) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID del médico es requerido");
            }
            
            // Validar que al menos un campo esté presente
            if (medicoDTO.getNombre() == null && medicoDTO.getApellido() == null && medicoDTO.getEspecialidad() == null) {
                return ResponseEntity.badRequest().body("Debe proporcionar al menos un campo para actualizar");
            }

            this.medicosService.update(id, medicoDTO);
            return ResponseEntity.ok().body("Médico actualizado correctamente");
        } catch (UpdateException updateException) {
            return ResponseEntity.badRequest().body(updateException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el médico: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("id") Integer id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID del médico es requerido");
            }
            this.medicosService.delete(id);
            return ResponseEntity.ok().body("Médico eliminado correctamente");
        } catch (DeleteException deleteException) {
            return ResponseEntity.badRequest().body(deleteException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el médico: " + e.getMessage());
        }
    }
}
