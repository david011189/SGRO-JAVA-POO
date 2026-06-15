package estructura;

public class Usuario {

    protected int id;
    protected String nombre;
    protected String correo;
    protected String password;

    public Usuario(String nombre, String correo, String password) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
    }


public boolean login(String correo, String password) {
    if (this.correo.equals(correo) && this.password.equals(password)) {
        System.out.println("Login exitoso");
        return true;
    } else {
        System.out.println("Credenciales incorrectas");
        return false;
    }
}


    public void registrar() {
        System.out.println("Usuario registrado: " + nombre);
    }
    
    public String getNombre() {
        return nombre;
    }
}