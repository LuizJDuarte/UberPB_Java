package com.uberpb.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ArmazenamentoDados {
  private static final String DIRETORIO = "data";
  private static final ArmazenamentoDados INSTANCIA = new ArmazenamentoDados();
  private final Path pastaBase;

  private ArmazenamentoDados() {
    this.pastaBase = Path.of(DIRETORIO);
    try {
      if (!Files.exists(pastaBase)) Files.createDirectories(pastaBase);
    } catch (IOException e) {
      throw new IllegalStateException("Não foi possível criar a pasta 'data/'", e);
    }
  }

  public static ArmazenamentoDados obter() { return INSTANCIA; }
  public Path resolver(String nomeArquivo) { return pastaBase.resolve(nomeArquivo); }
}
