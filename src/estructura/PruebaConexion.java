package estructura;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        Connection conn = ConexionBD.conectar();

        if (conn != null) {

            System.out.println("Todo funciona correctamente");

        }
    }
}