package ec.edu.espe.ProyectoClinica.repository;

import ec.edu.espe.ProyectoClinica.entity.PacientesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacientesRepository extends JpaRepository<PacientesEntity, Integer> {

    PacientesEntity findByPacienteEmail(String pacienteEmail);
}
