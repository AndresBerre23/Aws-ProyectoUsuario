package com.usuario.models.services;

import java.util.List;
import java.util.Optional;

import com.usuario.models.entity.Usuario;

public interface IUsuarioService {
    List<Usuario> findAll();
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id); 
    void delete(Long id);
}