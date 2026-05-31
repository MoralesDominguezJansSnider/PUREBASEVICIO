package avance.demo1.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "analisis_hematologico")
@Data @NoArgsConstructor @AllArgsConstructor
public class AnalisisHematologico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(precision = 5, scale = 2)
    private BigDecimal hemoglobina;

    @Column(precision = 5, scale = 2)
    private BigDecimal hematocrito;

    @Column(name = "ferritina_serica", precision = 7, scale = 2)
    private BigDecimal ferritinaSerica;

    @Column(name = "hierro_serico", precision = 7, scale = 2)
    private BigDecimal hierroSerico;

    @Column(name = "registrado_por_salud")
    private Boolean registradoPorSalud = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}