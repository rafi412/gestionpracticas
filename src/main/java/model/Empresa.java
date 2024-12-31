package model;

public class Empresa {
    private int idEmpresa;
    private int especialidad;
    private String nombre;
    private String direccion;
    private String correo;
    private String horario;
    private int plazasDisponibles;

    // Constructor vacío
    public Empresa() {
    }

    // Constructor con todos los campos
    public Empresa(int idEmpresa, int especialidad, String nombre, String direccion, String correo, String horario, int plazasDisponibles) {
        this.idEmpresa = idEmpresa;
        this.especialidad = especialidad;
        this.nombre = nombre;
        this.direccion = direccion;
        this.correo = correo;
        this.horario = horario;
        this.plazasDisponibles = plazasDisponibles;
    }

    // Getters y Setters
    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(int especialidad) {
        this.especialidad = especialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    // Método toString
    @Override
    public String toString() {
        return "Empresa{" +
                "idEmpresa=" + idEmpresa +
                ", especialidad=" + especialidad +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", correo='" + correo + '\'' +
                ", horario='" + horario + '\'' +
                ", plazasDisponibles=" + plazasDisponibles +
                '}';
    }
}
