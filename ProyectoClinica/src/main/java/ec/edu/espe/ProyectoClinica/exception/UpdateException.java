package ec.edu.espe.ProyectoClinica.exception;

public class UpdateException extends Exception{
    private String entityName;

    public UpdateException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }
}
