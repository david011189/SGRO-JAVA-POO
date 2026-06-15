package estructura;

import java.io.FileWriter;
import java.io.IOException;

public class ArchivoCliente {

    public static void guardarCliente(String nombre) {
        try {
            FileWriter writer = new FileWriter("clientes.txt", true);
            writer.write(nombre + "\n");
            writer.close();
            System.out.println("Cliente guardado en archivo");
        } catch (IOException e) {
            System.out.println("Error al guardar el cliente");
        }
    }
}