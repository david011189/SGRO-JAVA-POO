package estructura;





// RF-30: reserva formal del cliente (una estadía con una o varias habitaciones)
public class Reserva {

    private int id;
    private String fechaIngreso;
    private String fechaSalida;

    private Cliente cliente;
    private Habitacion habitacion;

    public Reserva(int id, Cliente cliente, String fechaIngreso, String fechaSalida) {
        this.id = id;
        this.cliente = cliente;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
    }

    public void asignarHabitacion(Habitacion habitacion) {
        if (habitacion.isDisponible()) {
            this.habitacion = habitacion;
            habitacion.setDisponible(false);
            System.out.println("Habitación asignada correctamente");
        } else {
            System.out.println("Habitación no disponible");
        }
    }

    public double calcularCosto() {
        if (habitacion != null) {
            return habitacion.getPrecio();
        } else {
            return 0;
        }
    }

    public void mostrarReserva() {
        System.out.println("Reserva ID: " + id);
        System.out.println("Cliente: " + cliente.nombre);
        System.out.println("Fecha ingreso: " + fechaIngreso);
        System.out.println("Fecha salida: " + fechaSalida);

        if (habitacion != null) {
            habitacion.mostrarInformacion();
        } else {
            System.out.println("Sin habitación asignada");
        }
    }
}