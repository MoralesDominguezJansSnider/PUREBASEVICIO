package avance.demo1.repository;

import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    List<Estudiante> findByPadre(Usuario padre);
    List<Estudiante> findByPadreId(Long padreId); 
}