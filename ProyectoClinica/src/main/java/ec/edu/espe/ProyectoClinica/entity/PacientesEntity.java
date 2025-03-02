package ec.edu.espe.ProyectoClinica.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pacientes")
public class PacientesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer pacienteId;

    @Column(name = "nombre")
    private String pacienteNombre;

    @Column(name = "apellido")
    private String pacienteApellido;

    @Column(name = "fecha_nacimiento")
    private Date pacientefechaNacimiento;

    @Column(name = "email")
    private String pacienteEmail;

    @OneToMany(mappedBy = "paciente")
    private List<CitasEntity> citas;

    public Integer getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Integer pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }

    public String getPacienteApellido() {
        return pacienteApellido;
    }

    public void setPacienteApellido(String pacienteApellido) {
        this.pacienteApellido = pacienteApellido;
    }

    public Date getPacientefechaNacimiento() {
        return pacientefechaNacimiento;
    }

    public void setPacientefechaNacimiento(Date pacientefechaNacimiento) {
        this.pacientefechaNacimiento = pacientefechaNacimiento;
    }

    public String getPacienteEmail() {
        return pacienteEmail;
    }

    public void setPacienteEmail(String pacienteEmail) {
        this.pacienteEmail = pacienteEmail;
    }

    public List<CitasEntity> getCitas() {
        return citas;
    }

    public void setCitas(List<CitasEntity> citas) {
        this.citas = citas;
    }
}
