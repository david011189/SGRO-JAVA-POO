package estructura;

import java.text.SimpleDateFormat;
import java.util.Date;
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

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Date ingreso = sdf.parse(fechaIngreso);
            Date salida = sdf.parse(fechaSalida);

            long diferencia = salida.getTime() - ingreso.getTime();

            long dias = diferencia / (1000 * 60 * 60 * 24);

            return dias * habitacion.getPrecio();

        } catch (Exception e) {

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