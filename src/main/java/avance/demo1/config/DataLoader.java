package avance.demo1.config;
import avance.demo1.model.AlimentoNutritivo;
import avance.demo1.repository.AlimentoNutritivoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
@Component
public class DataLoader implements CommandLineRunner {
    private final AlimentoNutritivoRepository repo;
    public DataLoader(AlimentoNutritivoRepository repo) {
        this.repo = repo;
    }
    @Override
    public void run(String... args) throws Exception {
        if (repo.count() < 5) {
            cargarDesdeCSV();
        }
    }
    private void cargarDesdeCSV() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("alimentos.csv");
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    resource.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );
            String line;
            boolean firstLine = true;
            int lineasProcesadas = 0;
            while ((line = reader.readLine()) != null) {
                if(firstLine){
                    firstLine = false;
                    continue;
                }
                try {
                    AlimentoNutritivo alimento =
                            parsearLinea(line);
                    if(alimento != null){
                        repo.save(alimento);
                        lineasProcesadas++;
                    }
                }catch(Exception e){
                    System.err.println(
                            "Error en línea CSV: "
                            + line
                            + " -> "
                            + e.getMessage()
                    );
                }
            }
            reader.close();
            System.out.println(
                    "CSV cargado correctamente. Líneas procesadas: "
                    + lineasProcesadas
            );
        }catch(IOException e){
            System.err.println(
                    "Error al leer CSV: "
                    + e.getMessage()
            );
            crearDatosMinimos();
        }
    }
    private AlimentoNutritivo parsearLinea(String line){
        String[] campos =
                line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if(campos.length < 10){
            return null;
        }
        AlimentoNutritivo a =
                new AlimentoNutritivo();
        a.setNombre(
                limpiarComillas(campos[0].trim())
        );
        a.setCategoria(
                AlimentoNutritivo.Categoria.valueOf(
                        campos[1].trim()
                )
        );
        a.setDescripcion(
                limpiarComillas(campos[2].trim())
        );
        a.setHierro(
                new BigDecimal(campos[3].trim())
        );
        a.setProteinas(
                new BigDecimal(campos[4].trim())
        );
        a.setCalcio(
                new BigDecimal(campos[5].trim())
        );
        a.setVitaminaA(
                new BigDecimal(campos[6].trim())
        );
        a.setVitaminaC(
                new BigDecimal(campos[7].trim())
        );
        a.setImagenUrl(
                limpiarComillas(campos[8].trim())
        );
        // H012
        a.setPorcionRecomendada(
                limpiarComillas(campos[9].trim())
        );
        return a;
    }
    private String limpiarComillas(String texto){
        if(texto.startsWith("\"")
                && texto.endsWith("\"")){
            return texto.substring(
                    1,
                    texto.length()-1
            );
        }
        return texto;
    }
    private void crearDatosMinimos(){
        AlimentoNutritivo a1 =
                new AlimentoNutritivo();
        a1.setNombre("Lentejas");
        a1.setCategoria(
                AlimentoNutritivo.Categoria.LEGUMBRES
        );
        a1.setDescripcion(
                "Legumbre rica en hierro y proteínas"
        );
        a1.setHierro(
                new BigDecimal("3.3")
        );
        a1.setProteinas(
                new BigDecimal("9.0")
        );
        a1.setCalcio(
                new BigDecimal("19")
        );
        a1.setVitaminaA(
                BigDecimal.ZERO
        );
        a1.setVitaminaC(
                new BigDecimal("1.5")
        );
        a1.setImagenUrl(
                "/img/lentejas.jpg"
        );
        a1.setPorcionRecomendada(
                "1 taza cocida"
        );
        repo.save(a1);
        AlimentoNutritivo a2 =
                new AlimentoNutritivo();
        a2.setNombre("Espinaca");
        a2.setCategoria(
                AlimentoNutritivo.Categoria.VERDURAS
        );
        a2.setDescripcion(
                "Verdura alta en hierro y vitamina A"
        );
        a2.setHierro(
                new BigDecimal("2.7")
        );
        a2.setProteinas(
                new BigDecimal("2.9")
        );
        a2.setCalcio(
                new BigDecimal("99")
        );
        a2.setVitaminaA(
                new BigDecimal("469")
        );
        a2.setVitaminaC(
                new BigDecimal("28.1")
        );
        a2.setImagenUrl(
                "/img/espinaca.jpg"
        );
        a2.setPorcionRecomendada(
                "1 taza fresca"
        );
        repo.save(a2);
    }
}