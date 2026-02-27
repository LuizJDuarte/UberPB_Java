package com.uberpb.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.function.Function;

/** Utilidades de IO para repositórios baseados em arquivo texto (data/...). */
abstract class BaseRepositorioArquivo {

    protected static Path prepararArquivoEmData(String nomeArquivo) {
        Path pasta = Paths.get(System.getProperty("user.dir")).resolve("data");
        try { if (!Files.exists(pasta)) Files.createDirectories(pasta); }
        catch (IOException e) { throw new IllegalStateException("Não foi possível criar pasta data/", e); }
        return pasta.resolve(nomeArquivo);
    }

    protected static <T> void gravarAtomico(Path destino, List<T> itens, Function<T,String> encoder) {
        Path tmp = destino.getParent().resolve(destino.getFileName() + ".tmp");
        try (BufferedWriter bw = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (T it : itens) {
                bw.write(encoder.apply(it));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao gravar TMP: " + e.getMessage());
            return;
        }
        try {
            Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            try { Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING); }
            catch (IOException ex) { System.err.println("Erro ao substituir arquivo: " + ex.getMessage()); }
        } catch (IOException e) {
            System.err.println("Erro ao mover TMP: " + e.getMessage());
        }
    }

    protected static void lerLinhas(Path arquivo, java.util.function.Consumer<String> consumer) {
        if (!Files.exists(arquivo)) return;
        try (BufferedReader br = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.isBlank()) consumer.accept(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler " + arquivo + ": " + e.getMessage());
        }
    }
}
