package avance.demo1.model;


import jakarta.persistence.*;

@Entity
public class Comida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;
    
    public Comida() {}
    
    public Comida(String descripcion, Estudiante estudiante) {
        this.descripcion = descripcion;
        this.estudiante = estudiante;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
}