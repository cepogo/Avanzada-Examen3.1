package ec.edu.espe.ProyectoClinica.repository;
import ec.edu.espe.ProyectoClinica.entity.ConsultoriosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultoriosRepository extends JpaRepository<ConsultoriosEntity, Integer> {

    ConsultoriosEntity findByConsultorioNumero(String consultorioNumero);

    boolean existsByConsultorioNumeroAndConsultorioPiso(String numero, Integer piso);
}