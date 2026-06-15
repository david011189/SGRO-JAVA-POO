package estructura;

import java.util.ArrayList;

public class MantenimientoHabitacion {

    private ArrayList<Habitacion> listaHabitaciones = new ArrayList<>();

    public void registrarHabitacion(Habitacion h) {
        listaHabitaciones.add(h);
        System.out.println("Habitación registrada correctamente");
    }

    public void listarHabitaciones() {
        System.out.println("Listado de habitaciones:");
        for (Habitacion h : listaHabitaciones) {
            h.mostrarInformacion();
        }
    }

    public void eliminarHabitacion(int numero) {
        boolean eliminado = listaHabitaciones.removeIf(h -> h.getNumero() == numero);

        if (eliminado) {
            System.out.println("Habitación eliminada");
        } else {
            System.out.println("Habitación no encontrada");
        }
    }
    
    public void actualizarDisponibilidad(int numero, boolean estado) {
        for (Habitacion h : listaHabitaciones) {
            if (h.getNumero() == numero) {
                h.setDisponible(estado);
                System.out.println("Disponibilidad actualizada");
                return;
            }
        }
        System.out.println("Habitación no encontrada");
    }
   

// ✅ NUEVO: actualizar precio
    
    public void actualizarPrecio(int numero, double nuevoPrecio) {
        for (Habitacion h : listaHabitaciones) {
            if (h.getNumero() == numero) {
                h.setPrecio(nuevoPrecio);
                System.out.println("Precio actualizado");
                return;
            }
        }
        System.out.println("Habitación no encontrada");
    }

}

