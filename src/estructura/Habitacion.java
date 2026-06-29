package estructura;

// RF-34 a RF-37: entidad Habitación del hospedaje
public class Habitacion {

    private int numero;
    private String tipo;
    private double precio;
    private boolean disponible;
    private int capacidad;
    private String descripcion;
    private String estado;

    public Habitacion(int numero, String tipo, double precio) {
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.disponible = true;
        this.estado = "disponible";
    }

    public int getNumero()          { return numero; }
    public String getTipo()         { return tipo; }
    public double getPrecio()       { return precio; }
    public boolean isDisponible()   { return disponible; }
    public int getCapacidad()       { return capacidad; }
    public String getDescripcion()  { return descripcion; }
    public String getEstado()       { return estado; }

    public void setNumero(int numero)           { this.numero = numero; }
    public void setTipo(String tipo)            { this.tipo = tipo; }
    public void setPrecio(double precio)        { this.precio = precio; }
    public void setDisponible(boolean disp)     { this.disponible = disp; }
    public void setCapacidad(int capacidad)     { this.capacidad = capacidad; }
    public void setDescripcion(String desc)     { this.descripcion = desc; }
    public void setEstado(String estado)        { this.estado = estado; }

    public void mostrarInformacion() { System.out.println(toString()); }

    @Override
    public String toString() {
        return "Habitación #" + numero + " - Tipo: " + tipo + " - Precio: S/" + precio;
    }
}
