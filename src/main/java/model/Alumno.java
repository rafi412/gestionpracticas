package model;

public class Alumno {
    private String dniAlumno;
    private String curso;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String direccion;
    private String correo;

    public Alumno(String dniAlumno, String curso, String nombre, String apellido, String fechaNacimiento, String direccion, String correo) {
        this.dniAlumno = dniAlumno;
        this.curso = curso;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.correo = correo;
    }

    // Getters y setters
    public String getDniAlumno() { return dniAlumno; }
    public void setDniAlumno(String dniAlumno) { this.dniAlumno = dniAlumno; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}