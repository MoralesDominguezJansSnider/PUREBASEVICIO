package avance.demo1.service;

import avance.demo1.model.*;
import avance.demo1.repository.AlimentoNutritivoRepository;
import avance.demo1.repository.AnalisisNutricionalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class AnalisisNutricionalService {

    private final AnalisisNutricionalRepository analisisRepository;
    private final AlimentoNutritivoRepository alimentoNutritivoRepository;

    // Valores diarios recomendados para niños (simplificados)
    private static final BigDecimal VDR_HIERRO = new BigDecimal("10");     // mg
    private static final BigDecimal VDR_VIT_C = new BigDecimal("40");      // mg
    private static final BigDecimal VDR_PROTEINAS = new BigDecimal("30");  // g
    private static final BigDecimal VDR_CALCIO = new BigDecimal("800");    // mg
    private static final BigDecimal VDR_VIT_A = new BigDecimal("400");     // mcg

    public AnalisisNutricionalService(AnalisisNutricionalRepository analisisRepository,
                                      AlimentoNutritivoRepository alimentoNutritivoRepository) {
        this.analisisRepository = analisisRepository;
        this.alimentoNutritivoRepository = alimentoNutritivoRepository;
    }

    public AnalisisNutricional calcularYGuardar(Comida comida) {
        String nombreAlimento = comida.getAlimento().toLowerCase().trim();
        BigDecimal porciones = comida.getPorciones() != null ? comida.getPorciones() : BigDecimal.ONE;

        // Buscar en la biblioteca de alimentos (ignorando mayúsculas/minúsculas)
        Optional<AlimentoNutritivo> optAlimento = alimentoNutritivoRepository
                .findByNombreContainingIgnoreCase(nombreAlimento)
                .stream()
                .findFirst();

        BigDecimal hierroPct = BigDecimal.ZERO;
        BigDecimal vitCPct = BigDecimal.ZERO;
        BigDecimal protPct = BigDecimal.ZERO;
        BigDecimal calcioPct = BigDecimal.ZERO;
        BigDecimal vitAPct = BigDecimal.ZERO;

        if (optAlimento.isPresent()) {
            AlimentoNutritivo alimento = optAlimento.get();

            // Los valores en la tabla están en mg, g, mcg por 100g. Calculamos porcentaje del VDR.
            // Suponemos que "porciones" es la cantidad en cientos de gramos (ej: 1 porción = 100g)
            hierroPct = alimento.getHierro().multiply(porciones)
                    .divide(VDR_HIERRO, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            vitCPct = alimento.getVitaminaC().multiply(porciones)
                    .divide(VDR_VIT_C, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            protPct = alimento.getProteinas().multiply(porciones)
                    .divide(VDR_PROTEINAS, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            calcioPct = alimento.getCalcio().multiply(porciones)
                    .divide(VDR_CALCIO, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            vitAPct = alimento.getVitaminaA().multiply(porciones)
                    .divide(VDR_VIT_A, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }
        // Si no se encuentra, se quedan en cero (podrías lanzar una excepción o loggear advertencia)

        AnalisisNutricional analisis = new AnalisisNutricional();
        analisis.setComida(comida);
        analisis.setHierroPct(hierroPct);
        analisis.setVitaminaCPct(vitCPct);
        analisis.setProteinasPct(protPct);
        analisis.setCalcioPct(calcioPct);
        analisis.setVitaminaAPct(vitAPct);

        return analisisRepository.save(analisis);
    }
}