package avance.demo1.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alimento_nutritivo")
@Data @NoArgsConstructor @AllArgsConstructor
public class AlimentoNutritivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(precision = 5, scale = 2)
    private BigDecimal hierro;

    @Column(precision = 5, scale = 2)
    private BigDecimal proteinas;

    @Column(precision = 5, scale = 2)
    private BigDecimal calcio;

    @Column(name = "vitamina_a", precision = 5, scale = 2)
    private BigDecimal vitaminaA;

    @Column(name = "vitamina_c", precision = 5, scale = 2)
    private BigDecimal vitaminaC;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    public enum Categoria {
        FRUTAS, VERDURAS, CARNES, CEREALES, LACTEOS, LEGUMBRES, PESCADOS
    }
}