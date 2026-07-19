package avance.demo1.controller;

import avance.demo1.model.AnalisisHematologico;
import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import avance.demo1.service.EstudianteService;
import avance.demo1.service.GeminiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/recomendaciones")
public class RecomendacionController {

    private final EstudianteService estudianteService;
    private final GeminiService geminiService;

    public RecomendacionController(EstudianteService estudianteService, GeminiService geminiService) {
        this.estudianteService = estudianteService;
        this.geminiService = geminiService;
    }

    @GetMapping("/{estudianteId}")
    public String verRecomendaciones(@PathVariable Long estudianteId,
                                     HttpSession session,
                                     Model model) {
        // Verificar que el usuario sea padre
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.PADRE) {
            return "redirect:/";
        }

        // Obtener el estudiante seleccionado y la lista de todos los estudiantes del padre
        Estudiante estudiante = estudianteService.obtenerPorId(estudianteId);
        List<Estudiante> estudiantes = estudianteService.listarPorPadre(usuario.getId());

        // Obtener el último análisis hematológico del estudiante
        List<AnalisisHematologico> analisisList = estudiante.getAnalisisHematologicos();
        AnalisisHematologico ultimoAnalisis = null;
        if (analisisList != null && !analisisList.isEmpty()) {
            ultimoAnalisis = analisisList.stream()
                    .max(Comparator.comparing(AnalisisHematologico::getFecha))
                    .orElse(null);
        }

        // Pasar los datos al modelo
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("ultimoAnalisis", ultimoAnalisis);

        return "recomendaciones/detalle";
    }

    @PostMapping("/generar")
    @ResponseBody
    public String generarRecomendacion(@RequestParam Long estudianteId) {
        Estudiante estudiante = estudianteService.obtenerPorId(estudianteId);
        return geminiService.generarRecomendaciones(estudiante);
    }
}