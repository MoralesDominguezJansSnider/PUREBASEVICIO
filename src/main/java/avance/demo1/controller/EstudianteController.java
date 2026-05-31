package avance.demo1.controller;

import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import avance.demo1.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.PADRE) return "redirect:/";
        List<Estudiante> estudiantes = estudianteService.listarPorPadre(usuario.getId());
        model.addAttribute("estudiantes", estudiantes);
        return "estudiantes/lista";
    }

    @GetMapping("/nuevo")
    public String formularioRegistro(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        // Enumeraciones para selects
        model.addAttribute("niveles", Estudiante.NivelSocioeconomico.values());
        model.addAttribute("situaciones", Estudiante.SituacionLaboral.values());
        model.addAttribute("accesos", Estudiante.AccesoAlimentos.values());
        model.addAttribute("ubicaciones", Estudiante.Ubicacion.values());
        model.addAttribute("culturas", Estudiante.CulturaAlimenticia.values());
        model.addAttribute("cocinas", Estudiante.TipoCocina.values());
        return "estudiantes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Estudiante estudiante, HttpSession session) {
        Usuario padre = (Usuario) session.getAttribute("usuario");
        estudiante.setPadre(padre);
        estudianteService.guardar(estudiante);
        return "redirect:/estudiantes";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Estudiante estudiante = estudianteService.obtenerPorId(id);
        model.addAttribute("estudiante", estudiante);
        return "estudiantes/detalle";
    }
}