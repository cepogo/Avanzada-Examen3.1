package ec.edu.espe.ProyectoClinica.entity;
import jakarta.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "citas")
public class CitasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer citasId;

    @ManyToOne
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private PacientesEntity paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private MedicosEntity medico;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false)
    private Date citaFecha;

    @Column(name = "hora", nullable = false)
    private Time citaHora;

    @ManyToOne
    @JoinColumn(name = "consultorio_id", referencedColumnName = "id")
    private ConsultoriosEntity consultorio;

    public CitasEntity() {
    }

    public Integer getCitasId() {
        return citasId;
    }

    public void setCitasId(Integer citasId) {
        this.citasId = citasId;
    }

    public PacientesEntity getPaciente() {
        return paciente;
    }

    public void setPaciente(PacientesEntity paciente) {
        this.paciente = paciente;
    }

    public MedicosEntity getMedico() {
        return medico;
    }

    public void setMedico(MedicosEntity medico) {
        this.medico = medico;
    }

    public Date getCitaFecha() {
        return citaFecha;
    }

    public void setCitaFecha(Date citaFecha) {
        this.citaFecha = citaFecha;
    }

    public Time getCitaHora() {
        return citaHora;
    }

    public void setCitaHora(Time citaHora) {
        this.citaHora = citaHora;
    }

    public ConsultoriosEntity getConsultorio() {
        return consultorio;
    }

    public void setConsultorio(ConsultoriosEntity consultorio) {
        this.consultorio = consultorio;
    }

    @Override
    public String toString() {
        return "CitasEntity{" +
                "citasId=" + citasId +
                ", paciente=" + (paciente != null ? paciente.getPacienteId() : null) +
                ", medico=" + (medico != null ? medico.getMedicoId() : null) +
                ", citaFecha=" + citaFecha +
                ", citaHora=" + citaHora +
                ", consultorio=" + (consultorio != null ? consultorio.getConsultorioId() : null) +
                '}';
    }
}
