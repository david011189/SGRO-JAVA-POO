package estructura;

public class Main {

    public static void main(String[] args) {

        // Crear cliente
        Cliente cliente = new Cliente("Luis", "javier@gmail.com", "123456", "12345678");
        Cliente cliente1 = new Cliente("Jose", "javier@gmail.com", "123456", "12345678");
        cliente.registrar();
        //práctica de manejo de archivos en Java
        ArchivoCliente.guardarCliente(cliente.getNombre());
        boolean acceso = cliente.login("javier@gmail.com", "123456");
        
        if (!acceso) {
            System.out.println("Acceso denegado. Verifique sus credenciales.");
            return;
        }

        System.out.println("------------------------");

        // Crear habitaciones
        Habitacion h1 = new Habitacion(101, "Simple", 70);
        Habitacion h2 = new Habitacion(102, "Doble", 90);

        // Mantenimiento
        MantenimientoHabitacion mantenimiento = new MantenimientoHabitacion();

        mantenimiento.registrarHabitacion(h1);
        mantenimiento.registrarHabitacion(h2);

        mantenimiento.listarHabitaciones();

        System.out.println("------------------------");
        

         // ✅ ACTUALIZAR PRECIO (AQUÍ VA)
         mantenimiento.actualizarPrecio(102, 120);

         // ✅ LISTAR OTRA VEZ PARA VER EL CAMBIO
         mantenimiento.listarHabitaciones();
        // Eliminar habitación
         System.out.println("------------------------");

      // ✅ ELIMINAR HABITACION
      mantenimiento.eliminarHabitacion(101);
      // ✅ LISTAR OTRA VEZ PARA VER CAMBIO
      mantenimiento.listarHabitaciones();
      
      //Mantenimiento cliente
      MantenimientoCliente mantenimientoCliente = new MantenimientoCliente();
      System.out.println("---- PRUEBA CLIENTES ----");
      mantenimientoCliente.registrarCliente(cliente);

   // intentar registrar duplicado
   mantenimientoCliente.registrarCliente(cliente1);

  
        // Crear reserva
        Reserva reserva = new Reserva(1, cliente, "01/06/2026", "11/06/2026");

        reserva.asignarHabitacion(h1);

        System.out.println("Costo total: " + reserva.calcularCosto());

        System.out.println("------------------------");

        reserva.mostrarReserva();
    }
}