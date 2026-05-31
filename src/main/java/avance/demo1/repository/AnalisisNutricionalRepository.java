package avance.demo1.repository;

import avance.demo1.model.AnalisisNutricional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalisisNutricionalRepository extends JpaRepository<AnalisisNutricional, Long> {
}