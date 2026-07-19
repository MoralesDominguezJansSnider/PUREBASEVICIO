package avance.demo1.repository;

import avance.demo1.model.AlimentoNutritivo;
import avance.demo1.model.AlimentoNutritivo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlimentoNutritivoRepository extends JpaRepository<AlimentoNutritivo, Long> {
    List<AlimentoNutritivo> findByCategoria(Categoria categoria);
    List<AlimentoNutritivo> findByNombreContainingIgnoreCase(String nombre);
    
}