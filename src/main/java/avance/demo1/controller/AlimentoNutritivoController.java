package avance.demo1.controller;

import avance.demo1.model.AlimentoNutritivo;
import avance.demo1.repository.AlimentoNutritivoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/alimentos")
public class AlimentoNutritivoController {

    private final AlimentoNutritivoRepository repo;

    public AlimentoNutritivoController(AlimentoNutritivoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String categoria,
                         @RequestParam(required = false) String buscar,
                         Model model) {
        List<AlimentoNutritivo> alimentos;
        if (buscar != null && !buscar.isEmpty()) {
            alimentos = repo.findByNombreContainingIgnoreCase(buscar);
        } else if (categoria != null && !categoria.isEmpty()) {
            alimentos = repo.findByCategoria(AlimentoNutritivo.Categoria.valueOf(categoria.toUpperCase()));
        } else {
            alimentos = repo.findAll();
        }
        model.addAttribute("alimentos", alimentos);
        model.addAttribute("categorias", AlimentoNutritivo.Categoria.values());
        return "alimentos/lista";
    }
}