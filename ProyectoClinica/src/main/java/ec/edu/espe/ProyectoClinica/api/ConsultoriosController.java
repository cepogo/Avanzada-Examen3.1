package ec.edu.espe.ProyectoClinica.api;

import ec.edu.espe.ProyectoClinica.api.dto.ConsultoriosDTO;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.service.ConsultoriosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/consultorios")
public class ConsultoriosController {

    private final ConsultoriosService consultoriosService;

    public ConsultoriosController(ConsultoriosService consultoriosService) {
        this.consultoriosService = consultoriosService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<ConsultoriosDTO>> getAllConsultorios() {
        try {
            List<ConsultoriosDTO> consultoriosDTOS = this.consultoriosService.getAllConsultorios();
            return ResponseEntity.ok().body(consultoriosDTOS);
        } catch (DocumentNotFoundException documentNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody ConsultoriosDTO consultoriosDTO) {
        try {
            if (consultoriosDTO.getNumero() == null || consultoriosDTO.getPiso() == null) {
                return ResponseEntity.badRequest().body("Todos los campos son requeridos");
            }
            this.consultoriosService.create(consultoriosDTO);
            return ResponseEntity.ok().body("Consultorio creado correctamente");
        } catch (InsertException insertException) {
            return ResponseEntity.badRequest().body(insertException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el consultorio: " + e.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<String> update(@RequestBody ConsultoriosDTO consultoriosDTO) {
        try {
            if (consultoriosDTO.getNumero() == null) {
                return ResponseEntity.badRequest().body("El número del consultorio es requerido");
            }
            this.consultoriosService.update(consultoriosDTO);
            return ResponseEntity.ok().body("Consultorio actualizado correctamente");
        } catch (UpdateException updateException) {
            return ResponseEntity.badRequest().body(updateException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el consultorio: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("id") Integer id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID del consultorio es requerido");
            }
            this.consultoriosService.delete(id);
            return ResponseEntity.ok().body("Consultorio eliminado correctamente");
        } catch (DeleteException deleteException) {
            return ResponseEntity.badRequest().body(deleteException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el consultorio: " + e.getMessage());
        }
    }
}
