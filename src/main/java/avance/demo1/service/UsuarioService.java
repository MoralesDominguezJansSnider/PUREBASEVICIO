package avance.demo1.service;

import avance.demo1.model.Usuario;
import avance.demo1.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    public Optional<Usuario> buscarPorCodigoVinculacion(String codigo) {
        return usuarioRepository.findByCodigoVinculacion(codigo);
    }
}