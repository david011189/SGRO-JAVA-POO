
package estructura;


// RF-34 a RF-37: entidad Habitación del hospedaje
public class Habitacion {

    private int numero;
    private String tipo;
    private double precio;
    private boolean disponible;

    public Habitacion(int numero, String tipo, double precio) {
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.disponible = true;
    }

    public int getNumero() {
        return numero;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void mostrarInformacion() {
        System.out.println(toString()); // usa el toString
    }
    
   

    @Override
    public String toString() {
        return "Habitación #" + numero + " - Tipo: " + tipo + " - Precio: " + precio;
    }
}