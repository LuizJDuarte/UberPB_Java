package com.uberpb.repository;

import com.uberpb.model.Usuario;

import java.util.List;

public interface RepositorioUsuario {
  void salvar(Usuario usuario);
  void atualizar(Usuario usuario);
  Usuario buscarPorEmail(String email);
  List<Usuario> buscarTodos();
}
