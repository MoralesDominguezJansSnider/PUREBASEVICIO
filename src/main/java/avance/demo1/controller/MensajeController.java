package avance.demo1.controller;

import avance.demo1.model.*;
import avance.demo1.repository.*;
import avance.demo1.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mensajes")
public class MensajeController {

    private final MensajeRepository mensajeRepo;
    private final UsuarioService usuarioService;
    private final VinculacionRepository vinculacionRepo;

    public MensajeController(MensajeRepository mensajeRepo, UsuarioService usuarioService,
                             VinculacionRepository vinculacionRepo) {
        this.mensajeRepo = mensajeRepo;
        this.usuarioService = usuarioService;
        this.vinculacionRepo = vinculacionRepo;
    }

    @GetMapping
    public String bandejaEntrada(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        List<Mensaje> recibidos = mensajeRepo.findByDestinatarioIdOrderByFechaEnvioDesc(usuario.getId());
        model.addAttribute("mensajes", recibidos);
        return "mensajes/bandeja";
    }

    @GetMapping("/enviar")
    public String formularioEnvio(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        // Obtener contactos vinculados
        List<Vinculacion> vinculaciones;
        if (usuario.getRol() == Usuario.Rol.PADRE) {
            vinculaciones = vinculacionRepo.findByPadreId(usuario.getId());
        } else {
            vinculaciones = vinculacionRepo.findByUsuarioVinculadoId(usuario.getId());
        }
        model.addAttribute("contactos", vinculaciones);
        model.addAttribute("mensaje", new Mensaje());
        return "mensajes/form";
    }

    @PostMapping("/enviar")
    public String enviar(@ModelAttribute Mensaje mensaje, HttpSession session) {
        Usuario remitente = (Usuario) session.getAttribute("usuario");
        mensaje.setRemitente(remitente);
        mensajeRepo.save(mensaje);
        return "redirect:/mensajes";
    }
}