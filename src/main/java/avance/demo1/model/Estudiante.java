package avance.demo1.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiante")
@Data @NoArgsConstructor @AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    private Integer edad;

    @Column(nullable = false, length = 1)
    private String sexo; // 'M' o 'F'

    private String grado;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_socioeconomico")
    private NivelSocioeconomico nivelSocioeconomico;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacion_laboral")
    private SituacionLaboral situacionLaboral;

    @Enumerated(EnumType.STRING)
    @Column(name = "acceso_alimentos")
    private AccesoAlimentos accesoAlimentos;

    @Column(name = "miembros_hogar")
    private Integer miembrosHogar;

    @Enumerated(EnumType.STRING)
    private Ubicacion ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "cultura_alimenticia")
    private CulturaAlimenticia culturaAlimenticia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cocina")
    private TipoCocina tipoCocina;

    @Column(name = "tiene_refrigeradora")
    private Boolean tieneRefrigeradora;

    @Column(name = "tiene_agua_potable")
    private Boolean tieneAguaPotable;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Relación con el padre (Usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id", nullable = false)
    private Usuario padre;

    // Relaciones uno a muchos
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<AnalisisHematologico> analisisHematologicos = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comida> comidas = new ArrayList<>();

    // Enumeraciones
    public enum NivelSocioeconomico {
        BAJO, MEDIO_BAJO, MEDIO, MEDIO_ALTO, ALTO
    }
    public enum SituacionLaboral {
        UNO_TRABAJA, AMBOS_TRABAJAN, NINGUNO_TRABAJA
    }
    public enum AccesoAlimentos {
        FACIL, MEDIO, DIFICIL
    }
    public enum Ubicacion {
        URBANO, RURAL
    }
    public enum CulturaAlimenticia {
        COSTA, SIERRA, SELVA
    }
    public enum TipoCocina {
        COMPLETA, BASICA, LENIA
    }
}