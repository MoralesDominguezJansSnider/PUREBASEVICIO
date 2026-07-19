package avance.demo1.controller;

import avance.demo1.model.Usuario;
import avance.demo1.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // Mostrar las opciones de rol (ahora incluye ADMIN)
        model.addAttribute("roles", Usuario.Rol.values());
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam Usuario.Rol rol,
                        HttpSession session,
                        Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.autenticar(email, password);
        if (usuarioOpt.isPresent() && usuarioOpt.get().getRol() == rol) {
            session.setAttribute("usuario", usuarioOpt.get());
            switch (rol) {
                case PADRE:
                    return "redirect:/estudiantes";
                case DOCENTE:
                case SALUD:
                    return "redirect:/vinculacion";
                case ADMIN:
                    return "redirect:/dashboard";
                default:
                    return "redirect:/";
            }
        } else {
            model.addAttribute("error", "Credenciales inválidas o rol incorrecto");
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}