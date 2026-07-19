package avance.demo1.repository;
import avance.demo1.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    // Mensajes recibidos
    List<Mensaje> findByDestinatarioIdOrderByFechaEnvioDesc(Long destinatarioId);
    // Mensajes enviados
    List<Mensaje> findByRemitenteIdOrderByFechaEnvioDesc(Long remitenteId);
    // Conversación entre dos usuarios
    @Query("""
            SELECT m 
            FROM Mensaje m
            WHERE 
            (m.remitente.id = :usuario1 
            AND 
            m.destinatario.id = :usuario2)
            OR
            (m.remitente.id = :usuario2 
            AND 
            m.destinatario.id = :usuario1)
            ORDER BY m.fechaEnvio ASC
            """)
    List<Mensaje> obtenerConversacion(
            Long usuario1,
            Long usuario2
    );
}