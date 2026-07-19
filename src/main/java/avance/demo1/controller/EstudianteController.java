package avance.demo1.controller;
import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import avance.demo1.model.AnalisisHematologico;
import avance.demo1.model.Vinculacion;
import avance.demo1.repository.VinculacionRepository;
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
    private final VinculacionRepository vinculacionRepository;
    public EstudianteController(
            EstudianteService estudianteService,
            VinculacionRepository vinculacionRepository
    ) {
        this.estudianteService = estudianteService;
        this.vinculacionRepository = vinculacionRepository;
    }
    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.PADRE)
            return "redirect:/";
        List<Estudiante> estudiantes =
                estudianteService.listarPorPadre(usuario.getId());
        int alto = 0;
        int medio = 0;
        int bajo = 0;
        for (Estudiante est : estudiantes) {
            if (est.getAnalisisHematologicos() == null ||
                est.getAnalisisHematologicos().isEmpty()) {
                continue;
            }
            AnalisisHematologico ultimo =
                    est.getAnalisisHematologicos()
                    .get(est.getAnalisisHematologicos().size() - 1);
            if (ultimo.getHemoglobina() == null) {
                continue;
            }
            double hb = ultimo.getHemoglobina().doubleValue();
            if (hb < 9) {
                alto++;
            } else if (hb < 12) {
                medio++;
            } else {
                bajo++;
            }
        }
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("alto", alto);
        model.addAttribute("medio", medio);
        model.addAttribute("bajo", bajo);
        return "estudiantes/lista";
    }
    @GetMapping("/nuevo")
    public String formularioRegistro(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("niveles",
                Estudiante.NivelSocioeconomico.values());
        model.addAttribute("situaciones",
                Estudiante.SituacionLaboral.values());
        model.addAttribute("accesos",
                Estudiante.AccesoAlimentos.values());
        model.addAttribute("ubicaciones",
                Estudiante.Ubicacion.values());
        model.addAttribute("culturas",
                Estudiante.CulturaAlimenticia.values());
        model.addAttribute("cocinas",
                Estudiante.TipoCocina.values());
        return "estudiantes/form";
    }
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Estudiante estudiante,
            HttpSession session
    ) {
        Usuario padre =
                (Usuario) session.getAttribute("usuario");
        estudiante.setPadre(padre);
        estudianteService.guardar(estudiante);
        return "redirect:/estudiantes";
    }
    // H039 - Visualizar participantes del seguimiento
    @GetMapping("/{id}")
    public String ver(
            @PathVariable Long id,
            Model model
    ) {
        Estudiante estudiante =
                estudianteService.obtenerPorId(id);
        List<Vinculacion> participantes =
                vinculacionRepository.findByPadreId(
                        estudiante.getPadre().getId()
                );
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("participantes", participantes);
        return "estudiantes/detalle";
    }
}