/*package avance.demo1.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private int edad;
    private String nivelSocioeconomico; // "Pobre", "Medio", "Rico"
    private int numHermanos;
    
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<Comida> comidas;
    
    // Constructores
    public Estudiante() {}
    
    public Estudiante(String nombre, String apellido, int edad, String nivelSocioeconomico, int numHermanos) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.nivelSocioeconomico = nivelSocioeconomico;
        this.numHermanos = numHermanos;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getNivelSocioeconomico() { return nivelSocioeconomico; }
    public void setNivelSocioeconomico(String nivelSocioeconomico) { this.nivelSocioeconomico = nivelSocioeconomico; }
    public int getNumHermanos() { return numHermanos; }
    public void setNumHermanos(int numHermanos) { this.numHermanos = numHermanos; }
    public List<Comida> getComidas() { return comidas; }
    public void setComidas(List<Comida> comidas) { this.comidas = comidas; }
}  */