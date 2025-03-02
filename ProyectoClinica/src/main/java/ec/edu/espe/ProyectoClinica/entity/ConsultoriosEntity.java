package ec.edu.espe.ProyectoClinica.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "consultorios")
public class ConsultoriosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer consultorioId;

    @Column(name = "numero", unique = true, nullable = false)
    private String consultorioNumero;

    @Column(name = "piso", nullable = false)
    private Integer consultorioPiso;

    @OneToMany(mappedBy = "consultorio")
    private List<CitasEntity> citas;

    public ConsultoriosEntity() {
    }

    public Integer getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(Integer consultorioId) {
        this.consultorioId = consultorioId;
    }

    public String getConsultorioNumero() {
        return consultorioNumero;
    }

    public void setConsultorioNumero(String consultorioNumero) {
        this.consultorioNumero = consultorioNumero;
    }

    public Integer getConsultorioPiso() {
        return consultorioPiso;
    }

    public void setConsultorioPiso(Integer consultorioPiso) {
        this.consultorioPiso = consultorioPiso;
    }

    public List<CitasEntity> getCitas() {
        return citas;
    }

    public void setCitas(List<CitasEntity> citas) {
        this.citas = citas;
    }

    @Override
    public String toString() {
        return "ConsultoriosEntity{" +
                "consultorioId=" + consultorioId +
                ", consultorioNumero='" + consultorioNumero + '\'' +
                ", consultorioPiso=" + consultorioPiso +
                '}';
    }
}
