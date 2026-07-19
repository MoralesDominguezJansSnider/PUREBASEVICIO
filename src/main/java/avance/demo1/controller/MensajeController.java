package avance.demo1.controller;
import avance.demo1.model.Mensaje;
import avance.demo1.model.Usuario;
import avance.demo1.model.Vinculacion;
import avance.demo1.repository.MensajeRepository;
import avance.demo1.repository.VinculacionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/mensajes")
public class MensajeController {
    private final MensajeRepository mensajeRepo;
    private final VinculacionRepository vinculacionRepo;
    public MensajeController(
            MensajeRepository mensajeRepo,
            VinculacionRepository vinculacionRepo
    ){
        this.mensajeRepo = mensajeRepo;
        this.vinculacionRepo = vinculacionRepo;
    }
    /*
     * Pantalla principal de mensajes
     * Muestra contactos
     */
    @GetMapping
    public String mensajes(
            HttpSession session,
            Model model
    ){
        Usuario usuario =
                (Usuario) session.getAttribute("usuario");
        if(usuario == null){
            return "redirect:/";
        }
        List<Vinculacion> contactos;
        if(usuario.getRol() == Usuario.Rol.PADRE){
            contactos =
            vinculacionRepo.findByPadreId(usuario.getId());
        }else{
            contactos =
            vinculacionRepo.findByUsuarioVinculadoId(usuario.getId());
        }
        model.addAttribute(
                "contactos",
                contactos
        );
        model.addAttribute(
                "usuarioActual",
                usuario
        );
        return "mensajes/bandeja";
    }
    /*
     * Abrir conversación
     */
    @GetMapping("/chat/{id}")
    public String abrirChat(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ){
        Usuario usuario =
                (Usuario) session.getAttribute("usuario");
        if(usuario == null){
            return "redirect:/";
        }
        List<Mensaje> mensajes =
                mensajeRepo.obtenerConversacion(
                        usuario.getId(),
                        id
                );
        List<Vinculacion> contactos;
        if(usuario.getRol() == Usuario.Rol.PADRE){
            contactos =
            vinculacionRepo.findByPadreId(usuario.getId());
        }else{
            contactos =
            vinculacionRepo.findByUsuarioVinculadoId(usuario.getId());
        }
        model.addAttribute(
                "mensajes",
                mensajes
        );
        model.addAttribute(
                "contactos",
                contactos
        );
        model.addAttribute(
                "contactoId",
                id
        );
        model.addAttribute(
                "usuarioActual",
                usuario
        );
        return "mensajes/chat";
    }
    /*
     * Enviar mensaje desde chat
     */
    @PostMapping("/chat/enviar")
    public String enviarMensaje(
            @RequestParam Long destinatarioId,
            @RequestParam String contenido,
            HttpSession session
    ){
        Usuario usuario =
                (Usuario) session.getAttribute("usuario");
        if(usuario == null){
            return "redirect:/";
        }
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(usuario);
        Usuario destinatario =
                new Usuario();
        destinatario.setId(destinatarioId);
        mensaje.setDestinatario(destinatario);
        mensaje.setContenido(contenido);
        mensajeRepo.save(mensaje);
        return "redirect:/mensajes/chat/" + destinatarioId;
    }
}