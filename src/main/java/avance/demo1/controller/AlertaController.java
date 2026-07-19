package avance.demo1.controller;
import avance.demo1.model.AnalisisHematologico;
import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import avance.demo1.repository.EstudianteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/alertas")
public class AlertaController {
    private final EstudianteRepository estudianteRepo;
    public AlertaController(EstudianteRepository estudianteRepo) {
        this.estudianteRepo = estudianteRepo;
    }
    @GetMapping
    public String listarAlertas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        // PRUEBA PARA VER SI ENTRA
        System.out.println("USUARIO EN ALERTAS: " + usuario);
        if (usuario != null) {
            System.out.println("ROL: " + usuario.getRol());
        }
        if (usuario == null ||
                (usuario.getRol() != Usuario.Rol.ADMIN &&
                 usuario.getRol() != Usuario.Rol.SALUD)) {
            return "redirect:/";
        }
        List<Estudiante> todos = estudianteRepo.findAllConAnalisis();
        List<Estudiante> criticos = todos.stream()
                .filter(e -> e.getAnalisisHematologicos() != null
                        && !e.getAnalisisHematologicos().isEmpty())
                .sorted(Comparator.comparingDouble(e -> {
                    AnalisisHematologico ultimo =
                            e.getAnalisisHematologicos()
                                    .stream()
                                    .max(Comparator.comparing(AnalisisHematologico::getFecha))
                                    .orElse(null);
                    if (ultimo == null ||
                        ultimo.getHemoglobina() == null) {
                        return 999;
                    }
                    return ultimo.getHemoglobina().doubleValue();
                }))
                .collect(Collectors.toList());
        model.addAttribute("criticos", criticos);
        model.addAttribute("total", todos.size());
        return "alertas/lista";
    }
}