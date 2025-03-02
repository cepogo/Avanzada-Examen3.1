package ec.edu.espe.ProyectoClinica.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.time.LocalDate;

public class PacientesDTO {

    private Integer id;

    private String nombre;

    private String apellido;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Guayaquil")
    private Date fechaNacimiento;

    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
