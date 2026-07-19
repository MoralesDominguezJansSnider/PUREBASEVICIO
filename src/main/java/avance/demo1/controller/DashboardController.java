package avance.demo1.controller;
import avance.demo1.model.AnalisisHematologico;
import avance.demo1.model.AnalisisNutricional;
import avance.demo1.model.Comida;
import avance.demo1.model.Estudiante;
import avance.demo1.model.Usuario;
import avance.demo1.repository.ComidaRepository;
import avance.demo1.repository.EstudianteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final EstudianteRepository estudianteRepo;
    private final ComidaRepository comidaRepo;
    public DashboardController(
            EstudianteRepository estudianteRepo,
            ComidaRepository comidaRepo
    ){
        this.estudianteRepo = estudianteRepo;
        this.comidaRepo = comidaRepo;
    }
    @GetMapping
    public String dashboard(
            HttpSession session,
            Model model
    ){
        Usuario usuario =
                (Usuario) session.getAttribute("usuario");
        if(usuario == null ||
           usuario.getRol() != Usuario.Rol.ADMIN){
            return "redirect:/";
        }
        List<Estudiante> estudiantes =
                estudianteRepo.findAll();
        /*
         =====================================
         H041 - ALERTAS CRÍTICAS
         Hemoglobina menor a 9
         =====================================
        */
        List<Estudiante> criticos =
                estudiantes.stream()
                .filter(e ->
                        e.getAnalisisHematologicos() != null &&
                        !e.getAnalisisHematologicos().isEmpty()
                )
                .filter(e -> {
                    AnalisisHematologico ultimo =
                            e.getAnalisisHematologicos()
                            .stream()
                            .max(
                                Comparator.comparing(
                                AnalisisHematologico::getFecha)
                            )
                            .orElse(null);
                    return ultimo != null &&
                           ultimo.getHemoglobina() != null &&
                           ultimo.getHemoglobina()
                           .compareTo(
                           new BigDecimal("9")) < 0;
                })
                .sorted(
                    Comparator.comparing(e ->
                        e.getAnalisisHematologicos()
                        .stream()
                        .max(
                        Comparator.comparing(
                        AnalisisHematologico::getFecha)
                        )
                        .get()
                        .getHemoglobina()
                    )
                )
                .collect(Collectors.toList());
        /*
         =====================================
         H042 - OBSERVACIONES GENERALES
         =====================================
        */
        List<Estudiante> observados =
                estudiantes.stream()
                .filter(e ->
                        e.getObservaciones() != null &&
                        !e.getObservaciones()
                        .trim()
                        .isEmpty()
                )
                .collect(Collectors.toList());
        /*
         =====================================
         H043 - MÉTRICAS NUTRICIONALES
         =====================================
        */
        List<Comida> comidas =
                comidaRepo.findAll();
        BigDecimal hierro =
                BigDecimal.ZERO;
        BigDecimal vitaminaC =
                BigDecimal.ZERO;
        BigDecimal proteinas =
                BigDecimal.ZERO;
        BigDecimal calcio =
                BigDecimal.ZERO;
        BigDecimal vitaminaA =
                BigDecimal.ZERO;
        int cantidad = 0;
        for(Comida comida : comidas){
            AnalisisNutricional analisis =
                    comida.getAnalisisNutricional();
            if(analisis != null){
                hierro =
                hierro.add(
                valor(analisis.getHierroPct()));
                vitaminaC =
                vitaminaC.add(
                valor(analisis.getVitaminaCPct()));
                proteinas =
                proteinas.add(
                valor(analisis.getProteinasPct()));
                calcio =
                calcio.add(
                valor(analisis.getCalcioPct()));
                vitaminaA =
                vitaminaA.add(
                valor(analisis.getVitaminaAPct()));
                cantidad++;
            }
        }
        List<BigDecimal> promediosGlobales =
                new ArrayList<>();
        if(cantidad > 0){
            promediosGlobales.add(
                    hierro.divide(
                    BigDecimal.valueOf(cantidad),
                    2,
                    RoundingMode.HALF_UP)
            );
            promediosGlobales.add(
                    vitaminaC.divide(
                    BigDecimal.valueOf(cantidad),
                    2,
                    RoundingMode.HALF_UP)
            );
            promediosGlobales.add(
                    proteinas.divide(
                    BigDecimal.valueOf(cantidad),
                    2,
                    RoundingMode.HALF_UP)
            );
            promediosGlobales.add(
                    calcio.divide(
                    BigDecimal.valueOf(cantidad),
                    2,
                    RoundingMode.HALF_UP)
            );
            promediosGlobales.add(
                    vitaminaA.divide(
                    BigDecimal.valueOf(cantidad),
                    2,
                    RoundingMode.HALF_UP)
            );
        }else{
            for(int i = 0; i < 5; i++){
                promediosGlobales.add(
                        BigDecimal.ZERO
                );
            }
        }
        /*
         =====================================
         DATOS PARA THYMELEAF
         =====================================
        */
        model.addAttribute(
                "totalEstudiantes",
                estudiantes.size()
        );
        model.addAttribute(
                "totalCriticos",
                criticos.size()
        );
        model.addAttribute(
                "totalObservaciones",
                observados.size()
        );
        model.addAttribute(
                "criticos",
                criticos
        );
        model.addAttribute(
                "conObservaciones",
                observados
        );
        model.addAttribute(
                "promediosGlobales",
                promediosGlobales
        );
        return "dashboard/vistadash";
    }
    private BigDecimal valor(BigDecimal numero){
        return numero == null
                ? BigDecimal.ZERO
                : numero;
    }
}