package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;

public final class Autorizacao {
    private Autorizacao() {}

    public static boolean isAdmin(Usuario u) { return u instanceof Administrador; }

    public static void exigirAdmin(Usuario u) {
        if (!isAdmin(u)) throw new SecurityException("Ação permitida somente para ADMIN.");
    }
}
