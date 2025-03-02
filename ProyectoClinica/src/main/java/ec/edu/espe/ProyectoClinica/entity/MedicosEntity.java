package ec.edu.espe.ProyectoClinica.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "medicos")
public class
MedicosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer medicoId;

    @Column(name = "nombre")
    private String medicoNombre;

    @Column(name = "apellido")
    private String medicoApellido;

    @Column(name = "especialidad")
    private String medicoEspecialidad;

    @OneToMany(mappedBy = "medico")
    private List<CitasEntity> citas;

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }

    public void setMedicoNombre(String medicoNombre) {
        this.medicoNombre = medicoNombre;
    }

    public String getMedicoApellido() {
        return medicoApellido;
    }

    public void setMedicoApellido(String medicoApellido) {
        this.medicoApellido = medicoApellido;
    }

    public String getMedicoEspecialidad() {
        return medicoEspecialidad;
    }

    public void setMedicoEspecialidad(String medicoEspecialidad) {
        this.medicoEspecialidad = medicoEspecialidad;
    }

    public List<CitasEntity> getCitas() {
        return citas;
    }

    public void setCitas(List<CitasEntity> citas) {
        this.citas = citas;
    }
}
