package avance.demo1.repository;

import avance.demo1.model.Comida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComidaRepository extends JpaRepository<Comida, Long> {
}