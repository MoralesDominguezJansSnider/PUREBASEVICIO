package avance.demo1.controller;

import avance.demo1.model.*;
import avance.demo1.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final EstudianteRepository estudianteRepo;
    private final AnalisisHematologicoRepository analisisRepo;
    private final ComidaRepository comidaRepo;
    private final AlimentoNutritivoRepository alimentoNutritivoRepo;

    public AdminDashboardController(EstudianteRepository estudianteRepo,
                                    AnalisisHematologicoRepository analisisRepo,
                                    ComidaRepository comidaRepo,
                                    AlimentoNutritivoRepository alimentoNutritivoRepo) {
        this.estudianteRepo = estudianteRepo;
        this.analisisRepo = analisisRepo;
        this.comidaRepo = comidaRepo;
        this.alimentoNutritivoRepo = alimentoNutritivoRepo;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer month,
                            @RequestParam(required = false) Integer day,
                            HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || usuario.getRol() != Usuario.Rol.ADMIN) {
            return "redirect:/";
        }
        model.addAttribute("year", year != null ? year : 2026);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        return "dashboard/admin_dashboard";
    }

    // Endpoints AJAX para actualizar gráficos y tarjetas según filtros
    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getDashboardData(@RequestParam(required = false) Integer year,
                                                @RequestParam(required = false) Integer month,
                                                @RequestParam(required = false) Integer day) {
        Map<String, Object> result = new HashMap<>();

        // ---------- Filtro de fechas ----------
        List<Comida> comidasFiltradas = filtrarComidas(comidaRepo.findAll(), year, month, day);
        List<AnalisisHematologico> analisisFiltrados = filtrarAnalisis(analisisRepo.findAll(), year, month, day);

        // ---------- Tarjetas ----------
        result.put("totalEstudiantes", estudianteRepo.count());
        result.put("totalComidas", comidasFiltradas.size());
        double promedioHb = analisisFiltrados.stream()
                .filter(a -> a.getHemoglobina() != null)
                .mapToDouble(a -> a.getHemoglobina().doubleValue())
                .average().orElse(0.0);
        result.put("promedioHb", Math.round(promedioHb * 10.0) / 10.0);
        long estudiantesAnemia = analisisFiltrados.stream()
                .filter(a -> a.getHemoglobina() != null && a.getHemoglobina().compareTo(BigDecimal.valueOf(11)) < 0)
                .map(AnalisisHematologico::getEstudiante)
                .distinct().count();
        result.put("estudiantesAnemia", estudiantesAnemia);
        result.put("alimentosDistintos", comidasFiltradas.stream().map(Comida::getAlimento).distinct().count());
        // Recomendaciones IA (simulado, se podría contar mensajes generados)
        result.put("recomendacionesIA", 0); // Placeholder

        // ---------- Gráfico 1: Barras - Alimentos más consumidos ----------
        Map<String, Long> conteoAlimentos = comidasFiltradas.stream()
                .collect(Collectors.groupingBy(Comida::getAlimento, Collectors.counting()));
        List<String> labelsAlimentos = conteoAlimentos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<Long> dataAlimentos = labelsAlimentos.stream().map(conteoAlimentos::get).collect(Collectors.toList());
        result.put("labelsAlimentos", labelsAlimentos);
        result.put("dataAlimentos", dataAlimentos);

        // ---------- Gráfico 2: Línea - Promedio mensual de hemoglobina ----------
        Map<Integer, Double> promedioPorMes = new TreeMap<>();
        for (AnalisisHematologico a : analisisRepo.findAll()) { // usamos todos, no filtrados por mes para la evolución
            int m = a.getFecha().getMonthValue();
            promedioPorMes.compute(m, (k, v) -> {
                if (v == null) return a.getHemoglobina().doubleValue();
                return (v + a.getHemoglobina().doubleValue()) / 2.0;
            });
        }
        List<String> labelsMeses = new ArrayList<>();
        List<Double> dataHb = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : promedioPorMes.entrySet()) {
            labelsMeses.add(getMonthName(entry.getKey()));
            dataHb.add(Math.round(entry.getValue() * 100.0) / 100.0);
        }
        result.put("labelsMeses", labelsMeses);
        result.put("dataHb", dataHb);

        // ---------- Gráfico 3: Circular - Distribución de categorías ----------
        Map<String, Long> catCount = new HashMap<>();
        for (Comida c : comidasFiltradas) {
            Optional<AlimentoNutritivo> optAlim = alimentoNutritivoRepo.findByNombreContainingIgnoreCase(c.getAlimento())
                    .stream().findFirst();
            String cat = optAlim.isPresent() ? optAlim.get().getCategoria().toString() : "Sin categoría";
            catCount.merge(cat, 1L, Long::sum);
        }
        result.put("labelsCategorias", new ArrayList<>(catCount.keySet()));
        result.put("dataCategorias", new ArrayList<>(catCount.values()));

        // ---------- Predictivos ----------
        // Preparar datos mensuales completos (Julio-Sep)
        Map<Integer, Long> alimentosPorMes = new TreeMap<>();
        Map<Integer, Map<String, Long>> top5PorMes = new TreeMap<>();
        // Para hemoglobina, usar los promedios ya calculados por mes
        // Para alimentos más consumidos, proyectar top 5
        // Para categorías, proyectar distribución

        // 4. Barras predictivas (top 5 alimentos)
        List<String> labelsPredAlimentos = Arrays.asList("Octubre", "Noviembre", "Diciembre");
        List<Map<String, Object>> datasetsPredAlimentos = new ArrayList<>();
        // Obtener top 5 alimentos globales en Jul-Sep
        List<String> top5Global = conteoAlimentos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        for (String alimento : top5Global) {
            List<Long> predValues = new ArrayList<>();
            for (int m = 10; m <= 12; m++) {
                // Proyección lineal simple basada en tendencia mensual
                Long valor = predecirAlimento(alimento, conteoAlimentos, m);
                predValues.add(valor);
            }
            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", alimento);
            dataset.put("data", predValues);
            datasetsPredAlimentos.add(dataset);
        }
        result.put("labelsPredAlimentos", labelsPredAlimentos);
        result.put("datasetsPredAlimentos", datasetsPredAlimentos);

        // 5. Línea predictiva de hemoglobina
        List<Double> predHb = new ArrayList<>();
        for (int m = 10; m <= 12; m++) {
            double pred = predecirHemoglobina(promedioPorMes, m);
            predHb.add(Math.round(pred * 100.0) / 100.0);
        }
        result.put("predHb", predHb);

        // 6. Circular predictiva (distribución de categorías en diciembre)
        Map<String, Long> predCat = predecirCategorias(catCount);
        result.put("labelsPredCategorias", new ArrayList<>(predCat.keySet()));
        result.put("dataPredCategorias", new ArrayList<>(predCat.values()));

        return result;
    }

    // ---------- Métodos auxiliares de filtrado ----------
    private List<Comida> filtrarComidas(List<Comida> todas, Integer year, Integer month, Integer day) {
        return todas.stream().filter(c -> matchDate(c.getFecha(), year, month, day)).collect(Collectors.toList());
    }
    private List<AnalisisHematologico> filtrarAnalisis(List<AnalisisHematologico> todos, Integer year, Integer month, Integer day) {
        return todos.stream().filter(a -> matchDate(a.getFecha(), year, month, day)).collect(Collectors.toList());
    }
    private boolean matchDate(LocalDate fecha, Integer year, Integer month, Integer day) {
        if (year != null && fecha.getYear() != year) return false;
        if (month != null && fecha.getMonthValue() != month) return false;
        if (day != null && fecha.getDayOfMonth() != day) return false;
        return true;
    }

    private String getMonthName(int month) {
        String[] nombres = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        return (month >= 1 && month <= 12) ? nombres[month-1] : "Mes" + month;
    }

    // Regresión lineal simple para proyecciones
    private double predecirHemoglobina(Map<Integer, Double> promedios, int mesObjetivo) {
        List<Integer> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : promedios.entrySet()) {
            if (e.getKey() >= 7 && e.getKey() <= 9) {
                x.add(e.getKey());
                y.add(e.getValue());
            }
        }
        if (x.size() < 2) return promedios.getOrDefault(9, 10.0); // fallback
        double pendiente = (y.get(y.size()-1) - y.get(0)) / (x.get(x.size()-1) - x.get(0));
        double intercepto = y.get(0) - pendiente * x.get(0);
        return intercepto + pendiente * mesObjetivo;
    }

    private Long predecirAlimento(String alimento, Map<String, Long> conteoTotal, int mesObjetivo) {
        // Como no tenemos conteo mensual almacenado, usaremos una simplificación:
        // consideramos que el conteo total es para 3 meses y proyectamos con un factor de crecimiento.
        // En una implementación real habría que contar por mes.
        // Simularemos: obtener tendencia usando total/3 y aplicar crecimiento mensual del 5%.
        double total = conteoTotal.getOrDefault(alimento, 0L);
        double baseMensual = total / 3.0;
        // Factor de crecimiento lineal basado en la pendiente de los datos globales
        // Usaremos un incremento fijo del 10% mensual solo para propósitos demostrativos.
        return Math.round(baseMensual * (1 + 0.1 * (mesObjetivo - 7)));
    }

    private Map<String, Long> predecirCategorias(Map<String, Long> catActual) {
        // Proyectamos las mismas proporciones que septiembre (último mes) con un ligero aumento
        long total = catActual.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Long> pred = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : catActual.entrySet()) {
            pred.put(entry.getKey(), Math.round(entry.getValue() * 1.2));
        }
        return pred;
    }
}