package avance.demo1.controller;

import avance.demo1.model.*;
import avance.demo1.repository.AnalisisHematologicoRepository;
import avance.demo1.service.EstudianteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hematologia")
public class HematologiaController {

    private final AnalisisHematologicoRepository analisisRepo;
    private final EstudianteService estudianteService;

    public HematologiaController(AnalisisHematologicoRepository analisisRepo, EstudianteService estudianteService) {
        this.analisisRepo = analisisRepo;
        this.estudianteService = estudianteService;
    }

    @GetMapping("/{estudianteId}")
    public String listar(@PathVariable Long estudianteId, Model model) {
        List<AnalisisHematologico> analisis = analisisRepo.findByEstudianteId(estudianteId);
        model.addAttribute("analisis", analisis);
        model.addAttribute("estudiante", estudianteService.obtenerPorId(estudianteId));
        return "hematologia/lista";
    }

    @GetMapping("/nuevo/{estudianteId}")
    public String formulario(@PathVariable Long estudianteId, Model model) {
        AnalisisHematologico analisis = new AnalisisHematologico();
        analisis.setEstudiante(estudianteService.obtenerPorId(estudianteId));
        model.addAttribute("analisis", analisis);
        return "hematologia/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute AnalisisHematologico analisis) {
        analisisRepo.save(analisis);
        return "redirect:/hematologia/" + analisis.getEstudiante().getId();
    }
}