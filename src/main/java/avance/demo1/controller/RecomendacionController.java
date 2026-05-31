package avance.demo1.controller;

import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import avance.demo1.service.EstudianteService;
import avance.demo1.service.GeminiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // Carga la vista dividida con el estudiante seleccionado y la lista de todos los estudiantes del padre
    @GetMapping("/{estudianteId}")
    public String verRecomendaciones(@PathVariable Long estudianteId,
                                     HttpSession session,
                                     Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.PADRE) {
            return "redirect:/";
        }

        Estudiante estudiante = estudianteService.obtenerPorId(estudianteId);
        List<Estudiante> estudiantes = estudianteService.listarPorPadre(usuario.getId());

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("estudiantes", estudiantes);
        return "recomendaciones/detalle";   // nueva vista unificada
    }

    // Endpoint para AJAX: devuelve solo el texto de la recomendación generada por Gemini
    @PostMapping("/generar")
    @ResponseBody
    public String generarRecomendacion(@RequestParam Long estudianteId) {
        Estudiante estudiante = estudianteService.obtenerPorId(estudianteId);
        return geminiService.generarRecomendaciones(estudiante);
    }
}