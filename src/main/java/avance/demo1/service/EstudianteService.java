package avance.demo1.service;

import avance.demo1.model.Estudiante;
import avance.demo1.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstudianteService {
    private final EstudianteRepository estudianteRepository;
    public EstudianteService(EstudianteRepository repo) { this.estudianteRepository = repo; }
    public Estudiante guardar(Estudiante e) { return estudianteRepository.save(e); }
    public List<Estudiante> listarPorPadre(Long padreId) {
        return estudianteRepository.findByPadreId(padreId);
    }
    public Estudiante obtenerPorId(Long id) {
        return estudianteRepository.findById(id).orElseThrow();
    }
}