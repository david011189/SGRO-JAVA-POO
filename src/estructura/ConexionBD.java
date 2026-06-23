package estructura;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sgro";

    private static final String USER = "root";

    // CAMBIA ESTO POR TU CONTRASEÑA DE MYSQL
    private static final String PASSWORD = "12345";

    public static Connection conectar() {

        try {

            Connection conexion =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conexión exitosa a MySQL");

            return conexion;

        } catch (Exception e) {

            System.out.println("Error de conexión");
            e.printStackTrace();

            return null;
        }
    }
}