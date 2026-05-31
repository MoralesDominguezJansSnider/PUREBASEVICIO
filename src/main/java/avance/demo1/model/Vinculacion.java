package avance.demo1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vinculacion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"padre_id", "usuario_vinculado_id"}))
@Data @NoArgsConstructor @AllArgsConstructor
public class Vinculacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id", nullable = false)
    private Usuario padre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_vinculado_id", nullable = false)
    private Usuario usuarioVinculado;

    @Column(nullable = false, length = 10)
    private String codigo;
}