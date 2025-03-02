package ec.edu.espe.ProyectoClinica.api;

import ec.edu.espe.ProyectoClinica.api.dto.CitaDTO;
import ec.edu.espe.ProyectoClinica.exception.DeleteException;
import ec.edu.espe.ProyectoClinica.exception.DocumentNotFoundException;
import ec.edu.espe.ProyectoClinica.exception.InsertException;
import ec.edu.espe.ProyectoClinica.exception.UpdateException;
import ec.edu.espe.ProyectoClinica.service.CitasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/citas")
public class CitasController {
    private final CitasService citasService;

    public CitasController(CitasService citasService) {
        this.citasService = citasService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        try {
            List<CitaDTO> citaDTOS = this.citasService.getAllCitas();
            return ResponseEntity.ok().body(citaDTOS);
        } catch (DocumentNotFoundException documentNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody CitaDTO citaDTO) {
        try {
            // Validación básica de campos requeridos
            if (citaDTO == null) {
                return ResponseEntity.badRequest().body("Los datos de la cita son requeridos");
            }

            // La validación detallada se realiza en el servicio
            this.citasService.create(citaDTO);
            return ResponseEntity.ok().body("Cita creada correctamente");
        } catch (InsertException insertException) {
            return ResponseEntity.badRequest().body(insertException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la cita: " + e.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<String> update(@RequestBody CitaDTO citaDTO) {
        try {
            // Validación básica
            if (citaDTO == null) {
                return ResponseEntity.badRequest().body("Los datos de la cita son requeridos");
            }
            if (citaDTO.getId() == null) {
                return ResponseEntity.badRequest().body("El ID de la cita es requerido");
            }

            // La validación detallada se realiza en el servicio
            this.citasService.update(citaDTO.getId(), citaDTO);
            return ResponseEntity.ok().body("Cita actualizada correctamente");
        } catch (UpdateException updateException) {
            return ResponseEntity.badRequest().body(updateException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar la cita: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("id") Integer id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID de la cita es requerido");
            }
            
            // Eliminar la cita y devolver respuesta inmediata
            this.citasService.delete(id);
            return ResponseEntity.ok().body("Cita eliminada correctamente");
            
        } catch (DeleteException deleteException) {
            return ResponseEntity.badRequest().body(deleteException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar la cita: " + e.getMessage());
        }
    }
}
