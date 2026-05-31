package avance.demo1.controller;

import avance.demo1.model.*;
import avance.demo1.repository.ComidaRepository;
import avance.demo1.service.AnalisisNutricionalService;
import avance.demo1.service.EstudianteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comidas")
public class ComidaController {

    private final ComidaRepository comidaRepo;
    private final EstudianteService estudianteService;
    private final AnalisisNutricionalService analisisService;

    public ComidaController(ComidaRepository comidaRepo, EstudianteService estudianteService,
                            AnalisisNutricionalService analisisService) {
        this.comidaRepo = comidaRepo;
        this.estudianteService = estudianteService;
        this.analisisService = analisisService;
    }

    @GetMapping("/{estudianteId}")
    public String listar(@PathVariable Long estudianteId, Model model) {
        List<Comida> comidas = comidaRepo.findByEstudianteIdOrderByFechaDesc(estudianteId);
        model.addAttribute("comidas", comidas);
        model.addAttribute("estudiante", estudianteService.obtenerPorId(estudianteId));
        return "comidas/lista";
    }

    @GetMapping("/nuevo/{estudianteId}")
    public String formulario(@PathVariable Long estudianteId, Model model) {
        Comida comida = new Comida();
        comida.setEstudiante(estudianteService.obtenerPorId(estudianteId));
        model.addAttribute("comida", comida);
        model.addAttribute("tipos", Comida.TipoComida.values());
        return "comidas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Comida comida) {
        comida = comidaRepo.save(comida);
        // Calcular análisis nutricional automáticamente
        analisisService.calcularYGuardar(comida);
        return "redirect:/comidas/" + comida.getEstudiante().getId();
    }
}