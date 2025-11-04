package com.usuario.models.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.usuario.models.entity.Usuario;
import com.usuario.models.services.IUsuarioService;
import com.usuario.models.services.S3Service;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioRestController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private S3Service s3Service;

    // LISTAR
    @GetMapping("/usuarios")
    public List<Usuario> index() {
        return usuarioService.findAll();
    }

    // BUSCAR POR ID
    @GetMapping("/usuarios/{id}")
    public Usuario show(@PathVariable Long id) {
        return usuarioService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id
                ));
    }

    // CREAR USUARIO + SUBIR FOTO Y CEDULA 
    @PostMapping(value = "/usuarios", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario create(
            @RequestParam("nombre") String nombre,
            @RequestParam("clave") String clave,
            @RequestParam("email") String email,
            @RequestParam("estado") boolean estado,
            @RequestPart("foto") MultipartFile foto,
            @RequestPart("cedula") MultipartFile cedula
    ) throws IOException {

        //  Verificar bucket antes de subir
        s3Service.verificarBucket();

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setClave(clave);
        usuario.setEmail(email);
        usuario.setEstado(estado);

        String fotoUrl = s3Service.subirArchivo(foto, "fotos", "image/jpeg");
        String cedulaUrl = s3Service.subirArchivo(cedula, "cedulas", "application/pdf");

        usuario.setFotoUrl(fotoUrl);
        usuario.setCedulaUrl(cedulaUrl);

        return usuarioService.save(usuario);
    }

    // ACTUALIZAR
    @PutMapping("/usuarios/{id}")
    public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id) {
        Usuario usuarioActual = usuarioService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id
                ));

        // Actualización parcial (solo campos que vengan)
        if (usuario.getNombre() != null) usuarioActual.setNombre(usuario.getNombre());
        if (usuario.getClave() != null) usuarioActual.setClave(usuario.getClave());
        if (usuario.getEmail() != null) usuarioActual.setEmail(usuario.getEmail());
        if (usuario.getEstado() != null) usuarioActual.setEstado(usuario.getEstado());
        if (usuario.getFotoUrl() != null) usuarioActual.setFotoUrl(usuario.getFotoUrl());
        if (usuario.getCedulaUrl() != null) usuarioActual.setCedulaUrl(usuario.getCedulaUrl());

        return usuarioService.save(usuarioActual);
    }

    // ELIMINAR
    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!usuarioService.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        usuarioService.delete(id);
    }
}