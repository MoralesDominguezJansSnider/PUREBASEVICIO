package avance.demo1.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analisis_nutricional")
@Data @NoArgsConstructor @AllArgsConstructor
public class AnalisisNutricional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comida_id", nullable = false)
    private Comida comida;

    @Column(name = "hierro_pct", precision = 5, scale = 2)
    private BigDecimal hierroPct;

    @Column(name = "vitamina_c_pct", precision = 5, scale = 2)
    private BigDecimal vitaminaCPct;

    @Column(name = "proteinas_pct", precision = 5, scale = 2)
    private BigDecimal proteinasPct;

    @Column(name = "calcio_pct", precision = 5, scale = 2)
    private BigDecimal calcioPct;

    @Column(name = "vitamina_a_pct", precision = 5, scale = 2)
    private BigDecimal vitaminaAPct;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo = LocalDateTime.now();
}