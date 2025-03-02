package ec.edu.espe.ProyectoClinica.api.dto;

public class ConsultoriosDTO {

    private Integer id;

    private String numero;

    private Integer piso;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }
}
