package ec.edu.espe.ProyectoClinica.repository;

import ec.edu.espe.ProyectoClinica.entity.CitasEntity;
import ec.edu.espe.ProyectoClinica.entity.PacientesEntity;
import ec.edu.espe.ProyectoClinica.entity.MedicosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitasRepository extends JpaRepository<CitasEntity, Integer> {
    List<CitasEntity> findByPaciente(PacientesEntity paciente);
    List<CitasEntity> findByMedico(MedicosEntity medico);
}