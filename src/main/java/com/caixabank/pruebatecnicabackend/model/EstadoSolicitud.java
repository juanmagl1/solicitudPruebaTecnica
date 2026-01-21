package com.caixabank.pruebatecnicabackend.model;

public enum EstadoSolicitud {
    PENDIENTE,
    APROBADA,
    RECHAZADA,
    CANCELADA;

    /**
     * Indica si se puede transitar de este estado a otro estado.
     * @param nuevo el estado al que se puede transitar.
     * @return true si se puede transitar, false de lo contrario.
     */
    public boolean transicionarA(EstadoSolicitud nuevo) {
        return switch (this) {
            case PENDIENTE -> nuevo == APROBADA || nuevo == RECHAZADA;
            case APROBADA -> nuevo == CANCELADA;
            default -> false;
        };
    }
}
