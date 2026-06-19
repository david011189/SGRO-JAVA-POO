package estructura;

public class Cliente extends Usuario {

    private String dni;

    public Cliente(String nombre, String correo, String password, String dni) {
        super(nombre, correo, password);
        this.dni = dni;
    }

    public void verReservas() {
        System.out.println("Mostrando reservas del cliente");
    }
    
    public String getDni() {
        return dni;
    }
    
    //Se agrego para el mantenimiento del cliente
    public String getCorreo() {
        return correo;
               
    }
    
    @Override
    public void registrar() {
        System.out.println("Cliente registrado: " + nombre);
    }
    
}