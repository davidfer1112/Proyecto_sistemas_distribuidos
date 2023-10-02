package org.distribuidos.Mensaje;

import java.io.Serializable;
import java.util.Date;

public class Mensaje implements Serializable {
    private double medida;
    private Date hora;

    public Mensaje(double medida, Date hora) {
        this.medida = medida;
        this.hora = hora;
    }

    public double getMedida() {
        return medida;
    }

    public Date getHora() {
        return hora;
    }
}