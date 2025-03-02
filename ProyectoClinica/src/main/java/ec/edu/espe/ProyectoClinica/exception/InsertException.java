package ec.edu.espe.ProyectoClinica.exception;

public class InsertException extends Exception {
    private String entityName;

    public InsertException(String entityName, String message) {
        super(message);
        this.entityName = entityName;
    }
}
