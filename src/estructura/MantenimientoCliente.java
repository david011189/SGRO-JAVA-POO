package estructura;

import java.util.ArrayList;

public class MantenimientoCliente {

    private ArrayList<Cliente> listaClientes = new ArrayList<>();

    public void registrarCliente(Cliente nuevo) {

        for (Cliente c : listaClientes) {

            if (c.getCorreo().equals(nuevo.getCorreo())) {
                System.out.println("El correo ya está registrado");
                return;
            }
            
            else if 
            (c.getDni().equals(nuevo.getDni())) {
                System.out.println("El DNI ya está registrado");
                return;
            }       
        }
        listaClientes.add(nuevo);
        System.out.println("Cliente registrado correctamente");
    }
}
