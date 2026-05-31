package avance.demo1.repository;

import avance.demo1.model.Vinculacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VinculacionRepository extends JpaRepository<Vinculacion, Long> {
    Optional<Vinculacion> findByPadreIdAndUsuarioVinculadoId(Long padreId, Long usuarioVinculadoId);
    List<Vinculacion> findByPadreId(Long padreId);
    List<Vinculacion> findByUsuarioVinculadoId(Long usuarioVinculadoId);
}