package avance.demo1.controller;
import avance.demo1.model.*;
import avance.demo1.repository.*;
import avance.demo1.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
@Controller
@RequestMapping("/graficos")
public class GraficoController {
    private final EstudianteService estudianteService;
    private final AnalisisHematologicoRepository analisisRepo;
    private final ComidaRepository comidaRepo;
    public GraficoController(EstudianteService estudianteService,
                             AnalisisHematologicoRepository analisisRepo,
                             ComidaRepository comidaRepo) {
        this.estudianteService = estudianteService;
        this.analisisRepo = analisisRepo;
        this.comidaRepo = comidaRepo;
    }
    @GetMapping("/{estudianteId}")
    public String verGraficos(@PathVariable Long estudianteId,
                              HttpSession session,
                              Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.PADRE) {
            return "redirect:/";
        }
        Estudiante estudiante = estudianteService.obtenerPorId(estudianteId);
        if (!estudiante.getPadre().getId().equals(usuario.getId())) {
            return "redirect:/estudiantes";
        }
        // Datos para gráfico de evolución hematológica
        List<AnalisisHematologico> analisisList = analisisRepo.findByEstudianteIdOrderByFechaAsc(estudianteId);
        List<String> fechas = new ArrayList<>();
        List<BigDecimal> hemoglobinas = new ArrayList<>();
        List<BigDecimal> hematocritos = new ArrayList<>();
        for (AnalisisHematologico a : analisisList) {
            fechas.add(a.getFecha().toString());
            hemoglobinas.add(a.getHemoglobina() != null ? a.getHemoglobina() : BigDecimal.ZERO);
            hematocritos.add(a.getHematocrito() != null ? a.getHematocrito() : BigDecimal.ZERO);
        }
        // Datos para gráfico de promedios de nutrientes
        List<Comida> comidas = comidaRepo.findByEstudianteIdOrderByFechaDesc(estudianteId);
        Map<String, BigDecimal> sumaNutrientes = new HashMap<>();
        int countConAnalisis = 0;
        for (Comida c : comidas) {
            if (c.getAnalisisNutricional() != null) {
                AnalisisNutricional an = c.getAnalisisNutricional();
                sumaNutrientes.merge("hierro", an.getHierroPct(), BigDecimal::add);
                sumaNutrientes.merge("vitamina_c", an.getVitaminaCPct(), BigDecimal::add);
                sumaNutrientes.merge("proteinas", an.getProteinasPct(), BigDecimal::add);
                sumaNutrientes.merge("calcio", an.getCalcioPct(), BigDecimal::add);
                sumaNutrientes.merge("vitamina_a", an.getVitaminaAPct(), BigDecimal::add);
                countConAnalisis++;
            }
        }
        List<BigDecimal> promedios = new ArrayList<>();
        if (countConAnalisis > 0) {
            String[] nutrientes = {"hierro", "vitamina_c", "proteinas", "calcio", "vitamina_a"};
            for (String n : nutrientes) {
                BigDecimal suma = sumaNutrientes.getOrDefault(n, BigDecimal.ZERO);
                promedios.add(suma.divide(BigDecimal.valueOf(countConAnalisis), 2, RoundingMode.HALF_UP));
            }
        } else {
            for (int i = 0; i < 5; i++) promedios.add(BigDecimal.ZERO);
        }
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("fechas", fechas);
        model.addAttribute("hemoglobinas", hemoglobinas);
        model.addAttribute("hematocritos", hematocritos);
        model.addAttribute("promedios", promedios);
        return "graficos/ver";
    }
}