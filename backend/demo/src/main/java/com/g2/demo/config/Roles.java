package com.g2.demo.config;

import java.util.Set;

public final class Roles {

    public static final String GERENTE = "GERENTE";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ALMACEN = "ALMACEN";
    public static final String HOUSEKEEPING = "HOUSEKEEPING";
    public static final Set<String> VALIDOS = Set.of(GERENTE, ADMINISTRADOR, ALMACEN, HOUSEKEEPING);

    private Roles() {
    }
}
