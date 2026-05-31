package avance.demo1.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "comida")
@Data @NoArgsConstructor @AllArgsConstructor
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida tipo;

    @Column(nullable = false, length = 150)
    private String alimento;

    @Column(precision = 5, scale = 2)
    private BigDecimal porciones;

    @OneToOne(mappedBy = "comida", cascade = CascadeType.ALL, orphanRemoval = true)
    private AnalisisNutricional analisisNutricional;

    public enum TipoComida {
        DESAYUNO, ALMUERZO, CENA
    }
}