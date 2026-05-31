package avance.demo1.service;

import avance.demo1.config.GeminiConfig;
import avance.demo1.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final GeminiConfig geminiConfig;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiService(RestTemplate restTemplate, GeminiConfig geminiConfig) {
        this.restTemplate = restTemplate;
        this.geminiConfig = geminiConfig;
    }

    public String generarRecomendaciones(Estudiante estudiante) {
        // Construir contexto detallado del estudiante
        StringBuilder contexto = new StringBuilder();
        contexto.append("Estudiante: ").append(estudiante.getNombreCompleto())
                .append(", Edad: ").append(estudiante.getEdad())
                .append(", Sexo: ").append(estudiante.getSexo())
                .append(", Grado: ").append(estudiante.getGrado())
                .append(", Nivel socioeconómico: ").append(estudiante.getNivelSocioeconomico())
                .append(", Ubicación: ").append(estudiante.getUbicacion())
                .append(", Cultura alimenticia: ").append(estudiante.getCulturaAlimenticia())
                .append(", Acceso a alimentos: ").append(estudiante.getAccesoAlimentos())
                .append("\n");

        // Últimos análisis hematológicos (si hay)
        List<AnalisisHematologico> analisis = estudiante.getAnalisisHematologicos();
        if (analisis != null && !analisis.isEmpty()) {
            // Ordenar por fecha descendente y tomar el más reciente
            AnalisisHematologico ultimo = analisis.stream()
                    .sorted((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()))
                    .findFirst().get();
            contexto.append("Último análisis hematológico (fecha: ").append(ultimo.getFecha()).append("):\n");
            contexto.append("Hemoglobina: ").append(ultimo.getHemoglobina()).append(" g/dL\n");
            contexto.append("Hematocrito: ").append(ultimo.getHematocrito()).append(" %\n");
            contexto.append("Ferritina sérica: ").append(ultimo.getFerritinaSerica()).append(" ng/mL\n");
            contexto.append("Hierro sérico: ").append(ultimo.getHierroSerico()).append(" mcg/dL\n");
            contexto.append("Observaciones: ").append(ultimo.getObservaciones()).append("\n");
        } else {
            contexto.append("No hay análisis hematológicos registrados.\n");
        }

        // Últimas comidas (máximo 5)
        List<Comida> comidas = estudiante.getComidas();
        if (comidas != null && !comidas.isEmpty()) {
            contexto.append("Comidas recientes:\n");
            comidas.stream()
                    .sorted((c1, c2) -> c2.getFecha().compareTo(c1.getFecha()))
                    .limit(5)
                    .forEach(c -> contexto.append("- ").append(c.getTipo())
                            .append(": ").append(c.getAlimento())
                            .append(" (").append(c.getPorciones()).append(" porciones)\n"));
        } else {
            contexto.append("No hay comidas registradas.\n");
        }

        // Prompt final
        String prompt = "Eres un nutricionista experto. Analiza la siguiente información de un estudiante " +
                "y detecta posibles deficiencias nutricionales contrastando los datos de sangre con la alimentación. " +
                "Luego recomienda exactamente 5 alimentos o comidas que ayuden a mejorar su estado nutricional. " +
                "Para cada recomendación explica brevemente (1-2 frases) por qué es beneficiosa.\n\n" +
                "Información:\n" + contexto.toString() + "\n\n" +
                "Responde en este formato:\n" +
                "1. [Alimento/Comida] - [Razón]\n" +
                "2. [Alimento/Comida] - [Razón]\n" +
                "... hasta 5";

        // Construir solicitud a Gemini
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String fullUrl = apiUrl + "?key=" + geminiConfig.getApiKey();

        ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, entity, Map.class);
        Map<String, Object> body = response.getBody();
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> contentPart = (Map<String, Object>) firstCandidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentPart.get("parts");
        String generatedText = (String) parts.get(0).get("text");

        return generatedText;
    }
}