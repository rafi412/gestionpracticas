package model;

public class Comentario {

    private int idComentario;
    private int idEmpresa;
    private String fechaComentario;
    private String nota;

    public Comentario(int idComentario, int idEmpresa, String fechaComentario, String nota) {
        this.idComentario = idComentario;
        this.idEmpresa = idEmpresa;
        this.fechaComentario = fechaComentario;
        this.nota = nota;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}
