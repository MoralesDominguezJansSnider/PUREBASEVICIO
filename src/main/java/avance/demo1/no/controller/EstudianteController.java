/*package avance.demo1.controller;

import avance.demo1.model.Estudiante;
import avance.demo1.service.EstudianteService;
import avance.demo1.service.IAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EstudianteController {
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private IAService iaService;
    
    @GetMapping("/")
    public String verEstudiantes(Model model) {
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        return "index";
    }
    
    @PostMapping(value = "/recomendar", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String recomendar(@RequestParam Long estudianteId) {
        Estudiante e = estudianteService.obtenerPorId(estudianteId);
        if (e == null) {
            return "Estudiante no encontrado";
        }
        return iaService.generarRecomendacionNutricional(e);
    }
}   */