
package com.novaretail.usuarios.service;

import com.novaretail.usuarios.model.UsuarioModel;
import com.novaretail.usuarios.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioModel> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioModel> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public UsuarioModel registrarUsuario(UsuarioModel usuario) {

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
    throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.CONFLICT,
        "El correo ya esta registrado"
    );
}
        String contrasenaSegura =
                passwordEncoder.encode(usuario.getContrasena());

        usuario.setContrasena(contrasenaSegura);

        usuario.setId(null);
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public UsuarioModel actualizarUsuario(Long id, UsuarioModel datos) {

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());

        if (!usuario.getCorreo().equals(datos.getCorreo())) {
            if (usuarioRepository.existsByCorreo(datos.getCorreo())) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT,
                        "El correo ya esta registrado"
                );
            }
            usuario.setCorreo(datos.getCorreo());
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        usuarioRepository.delete(usuario);
    }
}

