package avance.demo1.controller;

import avance.demo1.model.*;
import avance.demo1.repository.VinculacionRepository;
import avance.demo1.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/vinculacion")
public class VinculacionController {

    private final UsuarioService usuarioService;
    private final VinculacionRepository vinculacionRepo;

    public VinculacionController(UsuarioService usuarioService, VinculacionRepository vinculacionRepo) {
        this.usuarioService = usuarioService;
        this.vinculacionRepo = vinculacionRepo;
    }

    @GetMapping
    public String formularioCodigo() {
        return "vinculacion/form";
    }

    @PostMapping
    public String vincular(@RequestParam String codigo, HttpSession session, Model model) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");
        if (usuarioActual.getRol() == Usuario.Rol.PADRE) {
            return "redirect:/";
        }
        Optional<Usuario> padreOpt = usuarioService.buscarPorCodigoVinculacion(codigo);
        if (padreOpt.isPresent()) {
            Usuario padre = padreOpt.get();
            // Verificar si ya existe vinculación
            Optional<Vinculacion> existente = vinculacionRepo
                    .findByPadreIdAndUsuarioVinculadoId(padre.getId(), usuarioActual.getId());
            if (existente.isEmpty()) {
                Vinculacion v = new Vinculacion();
                v.setPadre(padre);
                v.setUsuarioVinculado(usuarioActual);
                v.setCodigo(codigo);
                vinculacionRepo.save(v);
            }
            return "redirect:/mensajes";
        } else {
            model.addAttribute("error", "Código inválido");
            return "vinculacion/form";
        }
    }
}