package com.usuario.models.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.usuario.models.entity.Usuario;
import com.usuario.models.services.IUsuarioService;

@RestController
@RequestMapping("/api")
public class UsuarioRestController {

    @Autowired
    private IUsuarioService usuarioService;

//      Listar todos los usuarios
    @GetMapping("/usuarios")
    public List<Usuario> index() {
        return usuarioService.findAll();
    }

    //  Buscar usuario por ID
    @GetMapping("/usuarios/{id}")
    public Usuario show(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }
        return usuario;
    }

    //  Crear nuevo usuario
    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario create(@RequestBody Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio.");
        }
        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave es obligatoria.");
        }
        return usuarioService.save(usuario);
    }

    // Actualizar usuario
    @PutMapping("/usuarios/{id}")
    public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id) {
        Usuario usuarioActual = usuarioService.findById(id);
        if (usuarioActual == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }

        // Actualizar campos si vienen en la petición
        if (usuario.getNombre() != null) usuarioActual.setNombre(usuario.getNombre());
        if (usuario.getClave() != null) usuarioActual.setClave(usuario.getClave());
        if (usuario.getEmail() != null) usuarioActual.setEmail(usuario.getEmail());
        if (usuario.getEstado() != null) usuarioActual.setEstado(usuario.getEstado());

        return usuarioService.save(usuarioActual);
    }

    //  Eliminar usuario
    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id);
        }
        usuarioService.delete(id);
    }
}