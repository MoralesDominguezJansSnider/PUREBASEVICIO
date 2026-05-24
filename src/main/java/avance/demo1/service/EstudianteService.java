package avance.demo1.service;

import avance.demo1.model.Estudiante;
import avance.demo1.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstudianteService {
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }
    
    public Estudiante obtenerPorId(Long id) {
        return estudianteRepository.findById(id).orElse(null);
    }
}