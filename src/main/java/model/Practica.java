package model;

public class Practica {

    private int  idPractica;
    private String dniAlumno;
    private int idEmpresa;
    private String fechaInicio;
    private String fechaFin;

    public Practica(int idPractica , String dniAlumno, int idEmpresa, String fechaInicio, String fechaFin ) {
        this.idPractica = idPractica;
        this.dniAlumno = dniAlumno;
        this.idEmpresa = idEmpresa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;

    }

    public int getIdPractica() {
        return idPractica;
    }

    public void setIdPractica(int idPractica) {
        this.idPractica = idPractica;
    }

    public String getDniAlumno() {
        return dniAlumno;
    }

    public void setDniAlumno(String dniAlumno) {
        this.dniAlumno = dniAlumno;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    // Método toString
    @Override
    public String toString() {
        return "Pracica{" +
                "idPractica=" + idPractica +
                ", dniAlumno=" + dniAlumno +
                ", idEmpresa='" + idEmpresa + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                '}';
    }

}
