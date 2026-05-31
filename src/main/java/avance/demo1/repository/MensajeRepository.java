package avance.demo1.repository;

import avance.demo1.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByDestinatarioIdOrderByFechaEnvioDesc(Long destinatarioId);
    List<Mensaje> findByRemitenteIdOrderByFechaEnvioDesc(Long remitenteId);
}