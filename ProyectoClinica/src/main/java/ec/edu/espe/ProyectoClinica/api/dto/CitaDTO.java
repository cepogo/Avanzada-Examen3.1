package ec.edu.espe.ProyectoClinica.api.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class CitaDTO {
    private Integer id;
    
    @JsonProperty("paciente_id")
    private Integer pacienteId;

    @JsonProperty("medico_id")
    private Integer medicoId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Guayaquil")
    private Date fecha;

    private String hora;

    @JsonProperty("consultorio_id")
    private Integer consultorioId;

    public CitaDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Integer pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Integer getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(Integer consultorioId) {
        this.consultorioId = consultorioId;
    }

    @Override
    public String toString() {
        return "CitaDTO{" +
                "id=" + id +
                ", pacienteId=" + pacienteId +
                ", medicoId=" + medicoId +
                ", fecha=" + fecha +
                ", hora='" + hora + '\'' +
                ", consultorioId=" + consultorioId +
                '}';
    }
}
