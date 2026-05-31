package avance.demo1.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/img")
public class ImagenController {

    @GetMapping(value = "/**", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generarImagen(HttpServletRequest request) throws IOException {
        // Obtener la ruta relativa después de /img/
        String path = request.getRequestURI().substring("/img/".length());
        
        // Quitar extensión (.jpg, .png, etc.)
        String nombreAlimento = path.contains(".") ? path.substring(0, path.lastIndexOf('.')) : path;
        nombreAlimento = URLDecoder.decode(nombreAlimento, StandardCharsets.UTF_8);

        // Crear imagen de 300x200 con fondo de color único según el nombre
        BufferedImage img = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        int hash = nombreAlimento.hashCode();
        Color colorFondo = new Color((hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF);
        g2d.setColor(colorFondo);
        g2d.fillRect(0, 0, 300, 200);

        // Escribir el nombre centrado en blanco
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int x = 150 - fm.stringWidth(nombreAlimento) / 2;
        int y = 100 - fm.getHeight() / 2 + fm.getAscent();
        g2d.drawString(nombreAlimento, x, y);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}