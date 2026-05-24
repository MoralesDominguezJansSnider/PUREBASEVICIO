package avance.demo1.service;

import avance.demo1.model.Comida;
import avance.demo1.model.Estudiante;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IAService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    // Usaremos exactamente la URL y modelo que te dio la página de Google
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";
    
    public String generarRecomendacionNutricional(Estudiante estudiante) {
        // 1. Construir el prompt
        String comidasStr = estudiante.getComidas().stream()
                .map(Comida::getDescripcion)
                .collect(Collectors.joining(", "));
        
        String prompt = String.format(
            "El infante %s %s tiene %d años, nivel socioeconómico %s, tiene %d hermanos, " +
            "y actualmente ha comido: %s. " +
            "Recomienda EXACTAMENTE 5 alimentos ricos en hierro, calcio y vitaminas (A, C, B12) " +
            "para prevenir la anemia. " +
            "Explica brevemente por qué cada uno ayuda. " +
            "Responde en español, de forma clara y práctica para una madre o padre. " +
            "IMPORTANTE: No escribas introducciones ni saludos. Ve directamente a la lista numerada. " +
            "Formato: lista numerada con cada alimento y su explicación en una línea.",
            estudiante.getNombre(),
            estudiante.getApellido(),
            estudiante.getEdad(),
            estudiante.getNivelSocioeconomico(),
            estudiante.getNumHermanos(),
            comidasStr.isEmpty() ? "nada aún hoy" : comidasStr
        );
        
        try {
            // 2. Configurar la llamada a la API
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Pasamos la API Key en los headers, tal como lo hace tu comando curl
            headers.set("x-goog-api-key", apiKey);
            
            // 3. Crear el cuerpo de la petición (Request Body)
            Map<String, Object> requestBody = new HashMap<>();
            
            // Instrucción del sistema (System Instruction)
            Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", "Eres un nutricionista pediátrico experto en prevención de anemia infantil."))
            );
            requestBody.put("systemInstruction", systemInstruction);
            
            // Contenido del usuario
            Map<String, Object> userContent = Map.of(
                "parts", List.of(Map.of("text", prompt))
            );
            requestBody.put("contents", List.of(userContent));
            
            // Configuración de generación
            Map<String, Object> generationConfig = Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 3000 // Aumentado a 1000 para que no se corte
            );
            requestBody.put("generationConfig", generationConfig);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 4. Realizar la petición POST a Gemini
            ResponseEntity<Map> response = restTemplate.exchange(
                GEMINI_URL, // Ya no le concatenamos la key al final de la URL
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            // 5. Extraer la respuesta
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String textoRespuesta = (String) parts.get(0).get("text");
            
            // 👇 NUEVO: Imprimimos la respuesta en la consola de Spring Boot para revisar
            System.out.println("\n--- RESPUESTA DE LA IA ---");
            System.out.println(textoRespuesta);
            System.out.println("--------------------------\n");
            return textoRespuesta;
            
        } catch (Exception e) {
            return "❌ Error al conectar con la IA de Gemini: " + e.getMessage() + 
                   "\n\nRevisa la consola de Spring Boot para más detalles.";
        }
    }
}