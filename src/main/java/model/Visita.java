package model;

public class Visita {
    
    private int idVisita;
    private int idPractica;
    private String dniTutor;
    private String fechaVisita;
    private String observaciones;
    private String comentarioTutor;

    public Visita(int idVisita, int idPractica, String dniTutor, String fechaVisita, String observaciones, String comentarioTutor) {
        this.idVisita = idVisita;
        this.idPractica = idPractica;
        this.dniTutor = dniTutor;
        this.fechaVisita = fechaVisita;
        this.observaciones = observaciones;
        this.comentarioTutor = comentarioTutor;

    }

    public int getIdVisita() {
        return idVisita;
    }

    public int getIdPractica() {
        return idPractica;
    }

    public String getFechaVisita() {
        return fechaVisita;
    }

    public String getDniTutor() {
        return dniTutor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getComentarioTutor() {
        return comentarioTutor;
    }

    public void setIdVisita(int idVisita) {
        this.idVisita = idVisita;
    }

    public void setcomentarioTutor(String comentarioTutor) {
        this.comentarioTutor = comentarioTutor;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setFechaVisita(String fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public void setDniTutor(String dniTutor) {
        this.dniTutor = dniTutor;
    }

    public void setIdPractica(int idPractica) {
        this.idPractica = idPractica;
    }

    // Método toString
    @Override
    public String toString() {
        return "Visita{" +
                "idVisita=" + idVisita +
                ", idPractica=" + idPractica +
                ", dniTutor" + dniTutor + '\'' +
                ",  fechaVisita" + fechaVisita + '\'' +
                ", observaciones" + observaciones + '\'' +
                ", comentarioTutor" + comentarioTutor + '\'' +
                '}';
    }
    
}
