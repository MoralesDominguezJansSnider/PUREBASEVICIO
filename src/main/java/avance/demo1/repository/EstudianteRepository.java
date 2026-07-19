package avance.demo1.repository;

import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByPadre(Usuario padre);

    List<Estudiante> findByPadreId(Long padreId);


    @Query("""
        SELECT DISTINCT e
        FROM Estudiante e
        LEFT JOIN FETCH e.analisisHematologicos
    """)
    List<Estudiante> findAllConAnalisis();

}