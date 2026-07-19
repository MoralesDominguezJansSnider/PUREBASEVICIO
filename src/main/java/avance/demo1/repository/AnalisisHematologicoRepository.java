package avance.demo1.repository;

import avance.demo1.model.AnalisisHematologico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalisisHematologicoRepository extends JpaRepository<AnalisisHematologico, Long> {
    List<AnalisisHematologico> findByEstudianteId(Long estudianteId);
    List<AnalisisHematologico> findByEstudianteIdOrderByFechaAsc(Long estudianteId);
}