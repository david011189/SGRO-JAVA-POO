package estructura;

import java.sql.*;
import java.util.ArrayList;

public class MantenimientoCliente {

    // Retorna mensaje de error o null si fue exitoso
    public String registrarCliente(Cliente nuevo) {
        String sql = "INSERT INTO clientes (dni, nombre, correo, password, rol) VALUES (?, ?, ?, ?, 'cliente')";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevo.getDni());
            ps.setString(2, nuevo.getNombre());
            ps.setString(3, nuevo.getCorreo());
            ps.setString(4, nuevo.getPassword());
            ps.executeUpdate();
            return null;

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("dni"))
                return "El DNI ya está registrado.";
            if (e.getMessage().contains("correo"))
                return "El correo ya está registrado.";
            return "Dato duplicado: " + e.getMessage();
        } catch (Exception e) {
            return "Error al registrar: " + e.getMessage();
        }
    }

    // Retorna mensaje de error o null si fue exitoso
    public String modificarCliente(String dniOriginal, String nuevoNombre,
                                    String nuevoCorreo, String nuevaPassword, String nuevoDni) {
        String sql = "UPDATE clientes SET dni=?, nombre=?, correo=?, password=? WHERE dni=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoDni);
            ps.setString(2, nuevoNombre);
            ps.setString(3, nuevoCorreo);
            ps.setString(4, nuevaPassword);
            ps.setString(5, dniOriginal);
            int filas = ps.executeUpdate();
            if (filas == 0) return "Cliente no encontrado.";
            return null;

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("dni"))
                return "El DNI ya está en uso por otro cliente.";
            if (e.getMessage().contains("correo"))
                return "El correo ya está en uso por otro cliente.";
            return "Dato duplicado: " + e.getMessage();
        } catch (Exception e) {
            return "Error al modificar: " + e.getMessage();
        }
    }

    public boolean eliminarCliente(String dni) {
        String sql = "DELETE FROM clientes WHERE dni=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Cliente> getListaClientes() throws Exception {
        ArrayList<Cliente> lista = new ArrayList<>();
        Connection con = ConexionBD.conectar();
        if (con == null) throw new Exception("No se pudo conectar a la base de datos.\nVerifica que MySQL esté activo y que la contraseña en ConexionBD.java sea correcta.");

        String sql = "SELECT dni, nombre, correo, password FROM clientes WHERE rol='cliente' ORDER BY nombre";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("dni")
                ));
            }
        } finally {
            con.close();
        }
        return lista;
    }
}
