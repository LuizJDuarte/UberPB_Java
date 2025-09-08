package com.uberpb.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class ArquivosAtomicos {
  private ArquivosAtomicos() {}

  public static void escreverAtomicamente(Path destino, String conteudo) {
    Path tmp = destino.getParent().resolve(destino.getFileName() + ".tmp");
    try {
      Files.writeString(tmp, conteudo, StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
      try {
        Files.move(tmp, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Falha ao salvar arquivo: " + destino, e);
    }
  }
}